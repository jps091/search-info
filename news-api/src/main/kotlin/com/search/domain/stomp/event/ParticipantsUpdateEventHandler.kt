package com.search.domain.stomp.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.search.domain.stomp.event.dto.ParticipantsUpdateEvent
import org.springframework.context.annotation.Profile
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
@Profile("global")
class ParticipantsUpdateEventHandler : ChatEventHandler<ParticipantsUpdateEvent> {
    override val eventType: String get() = "participants"

    override fun convert(payload: String, objectMapper: ObjectMapper): ParticipantsUpdateEvent {
        return objectMapper.readValue(payload, ParticipantsUpdateEvent::class.java)
    }

    override fun handle(event: ParticipantsUpdateEvent, messagingTemplate: SimpMessageSendingOperations) {
        messagingTemplate.convertAndSend(
                "/ws/topic/${event.roomKeyword}/participants",
                event.participants
        )
    }
}