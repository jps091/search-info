package com.search.domain.chatroom.infrastructure

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import lombok.Builder
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "chat_rooms")
class ChatRoomEntity(
        @field:Id
        @field:GeneratedValue(strategy = GenerationType.IDENTITY)
        @field:Column(name = "chat_room_id")
        var id: Long? = null,

        @field:CreatedDate
        @field:Column(updatable = false, name = "registered_at", columnDefinition = "TIMESTAMP")
        var registeredAt: LocalDateTime? = null,

        @field:NotNull
        @field:Column(length = 20, name = "room_keyword", unique = true)
        var roomKeyword: String,

        @field:NotNull
        @Column(name = "is_group_chat")
        var isGroupChat: Boolean
) {
        constructor(roomKeyword: String) : this(
                id = null,
                registeredAt = null,
                roomKeyword = roomKeyword,
                isGroupChat = true
        )
}