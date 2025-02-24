package com.search.domain.stomp.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.search.domain.stomp.event.dto.ChatEvent
import org.springframework.messaging.simp.SimpMessageSendingOperations

interface ChatEventHandler<T : ChatEvent> {
    val eventType: String
    fun convert(payload: String, objectMapper: ObjectMapper): T
    fun handle(event: T, messagingTemplate: SimpMessageSendingOperations)
}