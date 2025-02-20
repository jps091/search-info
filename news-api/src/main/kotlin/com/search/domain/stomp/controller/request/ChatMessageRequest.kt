package com.search.domain.stomp.controller.request

data class ChatMessageRequest(
        val message: String,
        val userToken: String
)
