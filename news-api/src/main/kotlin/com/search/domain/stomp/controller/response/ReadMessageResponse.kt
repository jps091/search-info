package com.search.domain.stomp.controller.response

data class ReadMessageResponse(
        val messageId: String,
        val unreadCount: String
)
