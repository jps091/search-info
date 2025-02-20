package com.search.error

enum class ChatRoomErrorType(override val description: String): ErrorTypeIfs {
    ROOM_NOT_EXIST("채팅방이 존재하지 않습니다."),
}