package com.search.domain.chatroom.controller.response

import java.time.LocalDateTime

data class ChatRoomResponse(
        var roomKeyword: String,
        var participants: Set<String>,
        var maximumCount: Int
)
