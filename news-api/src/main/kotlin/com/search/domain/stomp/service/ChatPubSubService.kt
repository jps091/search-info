package com.search.domain.stomp.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.search.domain.chatmessage.service.ChatMessageService
import com.search.domain.stomp.controller.request.ChatMessageRequest
import com.search.domain.stomp.controller.response.ChatMessageResponse
import com.search.domain.stomp.controller.response.ReadMessageResponse
import com.search.domain.stomp.event.ChatEventHandler
import com.search.domain.stomp.event.dto.ChatEvent
import com.search.domain.stomp.event.dto.ChatMessageEvent
import com.search.domain.stomp.event.dto.ParticipantsUpdateEvent
import com.search.domain.stomp.event.dto.ReadMessageUpdateEvent
import com.search.error.ErrorType
import com.search.exception.ApiException
import jakarta.annotation.PostConstruct
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service

@Service
@Profile("global")
class ChatPubSubService(
        private val redissonClient: RedissonClient,
        private val messagingTemplate: SimpMessageSendingOperations,
        private val chatMessageService: ChatMessageService,
        private val eventHandlers: List<ChatEventHandler<out ChatEvent>>
) {
    private val objectMapper = ObjectMapper()

    @PostConstruct
    fun init(){
        val topic = redissonClient.getTopic("chat")
        topic.addListener(String::class.java) { channel, msg ->
            // 수신된 메시지는 기존 onMessage()와 동일한 로직으로 처리
            onMessage(msg)
        }
    }

    // 채팅 메시지 전파
    fun sendChatMessage(roomKeyword: String, body: ChatMessageRequest): ChatMessageResponse {
        val response = chatMessageService.saveInRedis(roomKeyword, body)
        // 실제 데이터 모델에 맞게 필드를 매핑합니다.
        val event = ChatMessageEvent(roomKeyword, response.message, response.userToken)
        publishEvent(event)
        return response
    }

    // 참여자 업데이트 전파
    fun sendParticipantsUpdate(roomKeyword: String, participants: Set<String>) {
        val event = ParticipantsUpdateEvent(roomKeyword, participants)
        publishEvent(event)
    }

    // 제네릭 이벤트 객체를 받아 JSON 문자열로 직렬화 후 publish
    private fun publishEvent(event: ChatEvent) {
        val payload = try {
            objectMapper.writeValueAsString(event)
        } catch (ex: JsonProcessingException) {
            throw ApiException("JSON 파싱 에러", ErrorType.INVALID_PARAMETER, 500)
        }
        val topic = redissonClient.getTopic("chat")
        topic.publish(payload)
    }

    // 수신 메시지 처리 (Redisson RTopic 리스너에서 호출)
    private fun onMessage(payload: String) {
        val jsonNode = try {
            objectMapper.readTree(payload)
        } catch (ex: JsonProcessingException) {
            throw ApiException("JSON 파싱 에러", ErrorType.INVALID_PARAMETER, 500)
        }
        // "type" 필드가 없으면 기본적으로 "chat" 타입으로 처리합니다.
        val type = jsonNode["type"]?.asText() ?: "chat"
        val handlerAny = handlerMap[type] ?: handlerMap["chat"] ?:
            throw ApiException("핸들러를 찾을 수 없습니다. (type=$type)", ErrorType.INVALID_PARAMETER, 500)
        val handler = handlerAny as ChatEventHandler<ChatEvent>
        val event = handler.convert(payload, objectMapper)
        handler.handle(event, messagingTemplate)
    }

    private val handlerMap: Map<String, ChatEventHandler<out ChatEvent>> by lazy {
        eventHandlers.associateBy { it.eventType }
    }
}