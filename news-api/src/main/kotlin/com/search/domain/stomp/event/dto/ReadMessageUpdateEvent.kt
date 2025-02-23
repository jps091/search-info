package com.search.domain.stomp.event.dto

data class ReadMessageUpdateEvent(
        override val roomKeyword: String,
        val messageId: String,
        val unreadCount: Int
) : ChatEvent() {
    val type: String = "read"
}
