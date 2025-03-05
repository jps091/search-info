package com.search.domain.stomp.controller

import com.search.domain.chatmessage.service.ChatMessageService
import com.search.domain.chatroom.service.ChatRoomService
import com.search.domain.stomp.controller.request.ChatMessageRequest
import com.search.domain.stomp.controller.request.ReadMessageRequest
import com.search.domain.stomp.controller.response.ChatMessageResponse
import com.search.domain.stomp.controller.response.ReadMessageResponse
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller
import java.security.Principal

@Controller
class StompController(
        private val chatMessageService: ChatMessageService,
        private val chatRoomService: ChatRoomService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @MessageMapping("/{roomKeyword}") // 클라이언트에서 특정 publish/roomKeyword 형태로 메시지를 발행시 MessageMapping 에서 수신
    @SendTo("/ws/topic/{roomKeyword}") // 해당 roomKeyword에 메시지를 발행하여 구독중인 클라이언트에게 메시지 전송
    fun sendMessage(@DestinationVariable roomKeyword: String,
                    body: ChatMessageRequest): ChatMessageResponse
    {
        log.info("[StompController] ChatMessageRequest = $body")
        return chatMessageService.saveInRedis(roomKeyword, body)
    }

    @MessageMapping("/{roomKeyword}/participants")
    @SendTo("/ws/topic/{roomKeyword}/participants")
    fun getParticipants(@DestinationVariable roomKeyword: String): Set<String>
    {
        val participants = chatRoomService.getParticipants(roomKeyword)
        log.info("[StompController] getParticipants = $participants")
        return participants
    }

    @MessageMapping("/{roomKeyword}/read/{messageId}")
    @SendTo("/ws/topic/{roomKeyword}/read")
    fun markMessageAsRead(
            @DestinationVariable roomKeyword: String,
            @DestinationVariable messageId: String,
            body: ReadMessageRequest // WebSocket 연결 사용자의 정보 (userToken 사용)
    ): ReadMessageResponse {
        val userToken = body.token

        // 메시지 읽음 처리 후, 읽지 않은 사용자 수를 반환받음
        val unreadCount = chatMessageService.markMessageAsRead(roomKeyword, messageId, userToken)

        log.info("Message read update: messageId=$messageId, unreadCount=$unreadCount")

        // 반환값: 메시지 ID와 읽지 않은 사용자 수 (모두 읽었을 경우 빈 문자열)
        return ReadMessageResponse(
                messageId = messageId,
                unreadCount = if (unreadCount == 0L) "" else unreadCount.toString()
        )
    }
}