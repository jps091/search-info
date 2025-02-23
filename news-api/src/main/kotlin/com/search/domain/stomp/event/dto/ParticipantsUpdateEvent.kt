package com.search.domain.stomp.event.dto

data class ParticipantsUpdateEvent(
        override val roomKeyword: String,
        val participants: Set<String>
) : ChatEvent() {
    val type: String = "participants"
}
