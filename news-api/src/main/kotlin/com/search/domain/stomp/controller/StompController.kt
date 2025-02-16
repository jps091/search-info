package com.search.domain.stomp.controller

import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class StompController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomId 형태로 메시지를 발행시 MessageMapping 에서 수신
    @SendTo("/ws/topic/{roomId}") // 해당 roomId에 메시지를 발행하여 구독중인 클라이언트에게 메시지 전송
    fun sendMessage(@DestinationVariable roomId: Long, message: String): String{
        log.info("[StompController] sendMessage = $message")
        return message
    }
}