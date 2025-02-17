package com.search.domain.chatroom.controller.response


data class ChatRoomResponse(
        var roomKeyword: String,
        var participants: Set<String>,
        var maximumCount: Int
)
