package com.search.domain.chatmessage.infrastructure

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "chat_messages")
class ChatMessageEntity(
        @field:Id
        @field:GeneratedValue(strategy = GenerationType.IDENTITY)
        @field:Column(name = "chat_message_id")
        var id: Long? = null,

        @field:CreatedDate
        @field:Column(updatable = false, name = "registered_at", columnDefinition = "TIMESTAMP")
        var registeredAt: LocalDateTime? = null,

        @field:NotNull
        @field:JoinColumn(name = "chat_room_id")
        var chatRoomId: Long,

        @field:NotNull
        @field:Column(length = 30, name = "user_token")
        var userToken: String,

        @field:NotNull
        @field:Column(length = 100, name = "content")
        var content: String,
)