package com.search.error

enum class ChatMessageErrorType(override val description: String): ErrorTypeIfs {
    MESSAGE_FORMAT_MISMATCH("메시지 형식이 일치하지 않습니다."),
}