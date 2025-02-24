package com.search.domain.stomp.controller

import com.search.domain.chatroom.service.ChatRoomService
import com.search.domain.stomp.controller.request.ChatMessageRequest
import com.search.domain.stomp.service.ChatPubSubService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
@Profile("global") // Redis를 통해 브로드캐스팅을 하기 때문에 @SendTo 사용X
class StompPubSubController(
        private val chatRoomService: ChatRoomService,
        private val chatPubSubService: ChatPubSubService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @MessageMapping("/{roomKeyword}")
    fun sendMessage(@DestinationVariable roomKeyword: String,
                    body: ChatMessageRequest)
    {
        log.info("[StompController] ChatMessageRequest = $body")
        chatPubSubService.sendChatMessage(roomKeyword, body)
    }

    @MessageMapping("/{roomKeyword}/participants")
    fun getParticipants(@DestinationVariable roomKeyword: String)
    {
        val participants = chatRoomService.getParticipants(roomKeyword)
        log.info("[StompController] getParticipants = $participants")
        chatPubSubService.sendParticipantsUpdate(roomKeyword, participants)
    }
}