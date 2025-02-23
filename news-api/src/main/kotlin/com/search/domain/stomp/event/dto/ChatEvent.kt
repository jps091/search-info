package com.search.domain.stomp.event.dto

sealed class ChatEvent {
    abstract val roomKeyword: String
}