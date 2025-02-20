package com.search.domain.chatmessage.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface ChatMessageJpaRepository: JpaRepository<ChatMessageEntity, Long> {
}