package com.search.domain.chatmessage.service

import com.search.config.redis.RedisUtils
import com.search.domain.chatmessage.infrastructure.ChatMessageEntity
import com.search.domain.chatmessage.infrastructure.ChatMessageJpaRepository
import com.search.error.ChatMessageErrorType
import com.search.exception.ApiException
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatMessageWriteBackService(
        private val chatMessageJpaRepository: ChatMessageJpaRepository,
        private val redisUtils: RedisUtils
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    fun writeBackMessagesToDB() {
        while (true) {
            // 메시지를 안전하게 옮김 (RPOPLPUSH)
            val message = redisUtils.rpoplpush(RedisUtils.CHAT_MESSAGES_KEY, RedisUtils.CHAT_PROCESSING_KEY) ?: break // 데이터가 없으면 종료
            try {
                // 메시지 파싱
                val (chatRoomId, userToken, chatMessage) = parseMessage(message)

                // RDB에 저장
                chatMessageJpaRepository.save(ChatMessageEntity(
                        chatRoomId = chatRoomId.toLong(),
                        userToken = userToken,
                        content = chatMessage)
                )

                // 저장 성공 시, `processingKey`에서 제거
                redisUtils.lrem(RedisUtils.CHAT_PROCESSING_KEY, 1, message)
                log.info("Successfully saved to DB and removed from Redis: $message")
            } catch (e: Exception) {
                log.error("Error saving message to DB: $message", e)
                break
            }
        }
    }

    private fun parseMessage(message: String): Triple<String, String, String> {
        // 메시지를 ":" 구분자로 최대 3개의 항목으로 분할
        val parts = message.split(":", limit = 3)

        if (parts.size < 3) {
            throw ApiException("메시지 형식 불일치: $message", ChatMessageErrorType.MESSAGE_FORMAT_MISMATCH, 400)
        }

        val chatRoomId = parts[0]
        val userToken = parts[1]
        val chatMessage = parts[2]

        return Triple(chatRoomId, userToken, chatMessage)
    }
}