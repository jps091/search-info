package com.search.domain.chatroom.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomJpaRepository: JpaRepository<ChatRoomEntity, Long> {
    fun existsByRoomKeyword(roomKeyword: String): Boolean
    fun findByRoomKeyword(roomKeyword: String): ChatRoomEntity?
}