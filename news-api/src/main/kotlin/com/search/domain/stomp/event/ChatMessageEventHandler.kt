package com.search.domain.stomp.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.search.domain.stomp.event.dto.ChatMessageEvent
import org.springframework.context.annotation.Profile
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
@Profile("global")
class ChatMessageEventHandler : ChatEventHandler<ChatMessageEvent> {
    override val eventType: String get() = "chat" // 기본값: type 필드가 없으면 채팅 메시지로 간주

    override fun convert(payload: String, objectMapper: ObjectMapper): ChatMessageEvent {
        return objectMapper.readValue(payload, ChatMessageEvent::class.java)
    }

    override fun handle(event: ChatMessageEvent, messagingTemplate: SimpMessageSendingOperations) {
        messagingTemplate.convertAndSend("/ws/topic/${event.roomKeyword}", event)
    }
}