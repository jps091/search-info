package com.search.domain.stomp.controller.response

data class ChatMessageResponse(
        val message: String,
        val userToken: String,
        val messageId: String,
        val roomKeyword: String
)
