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
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service

@Service
@Profile("global")
class ChatPubSubService(
        @Qualifier("chatPubSub") private val pubSubStringRedisTemplate: StringRedisTemplate,
        private val messagingTemplate: SimpMessageSendingOperations,
        private val chatMessageService: ChatMessageService,
        private val eventHandlers: List<ChatEventHandler<out ChatEvent>>
) {
    private val objectMapper = ObjectMapper()

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

    // Redis Pub/Sub에서 수신한 메시지를 처리 (핸들러로 위임)
    fun onMessage(message: Message, pattern: ByteArray?) {
        val payload = String(message.body)
        val jsonNode = try {
            objectMapper.readTree(payload)
        } catch (ex: JsonProcessingException) {
            throw ApiException("JSON 파싱 에러", ErrorType.INVALID_PARAMETER, 500)
        }
        // "type" 필드가 없으면 기본적으로 "chat" 타입으로 처리합니다.
        val type = jsonNode["type"]?.asText() ?: "chat"
        val handlerAny = handlerMap[type] ?: handlerMap["chat"]
            ?: throw ApiException("핸들러를 찾을 수 없습니다. (type=$type)", ErrorType.INVALID_PARAMETER, 500)

        val handler = handlerAny as ChatEventHandler<ChatEvent>
        val event = handler.convert(payload, objectMapper)
        handler.handle(event, messagingTemplate)
    }

    /** 메시지 발행 (Redis Pub/Sub 채널에 메시지를 보냄) */
    // 이벤트 핸들러들을 맵으로 구성 (키: eventType)
    private val handlerMap: Map<String, ChatEventHandler<out ChatEvent>> by lazy {
        eventHandlers.associateBy { it.eventType }
    }

    // 제네릭 이벤트 객체를 받아 JSON 문자열로 직렬화 후 publish
    private fun publishEvent(event: ChatEvent) {
        val payload = try {
            objectMapper.writeValueAsString(event)
        } catch (ex: JsonProcessingException) {
            throw ApiException("JSON 파싱 에러", ErrorType.INVALID_PARAMETER, 500)
        }
        pubSubStringRedisTemplate.convertAndSend("chat", payload)
    }
}