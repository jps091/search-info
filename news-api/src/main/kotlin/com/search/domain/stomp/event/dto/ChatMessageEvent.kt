package com.search.domain.stomp.event.dto

data class ChatMessageEvent(
        override val roomKeyword: String,
        val message: String,
        val userToken: String
        // 필요한 다른 필드 추가 가능
) : ChatEvent()
