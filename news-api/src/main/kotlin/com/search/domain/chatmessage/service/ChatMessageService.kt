package com.search.domain.chatmessage.service

import com.search.config.redis.RedisUtils
import com.search.domain.chatroom.infrastructure.ChatRoomJpaRepository
import com.search.domain.chatroom.service.ChatRoomService
import com.search.domain.stomp.controller.request.ChatMessageRequest
import com.search.domain.stomp.controller.response.ChatMessageResponse
import com.search.error.ChatRoomErrorType
import com.search.exception.ApiException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatMessageService(
        private val chatRoomJpaRepository: ChatRoomJpaRepository,
        private val redisUtils: RedisUtils,
        private val chatRoomService: ChatRoomService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional(readOnly = true)
    fun saveInRedis(roomKeyword: String, body: ChatMessageRequest): ChatMessageResponse{
        val chatRoomEntity = chatRoomJpaRepository.findByRoomKeyword(roomKeyword) ?:
                                throw ApiException("채팅방 존재 하지 않음", ChatRoomErrorType.ROOM_NOT_EXIST, 404)

        val value = createRedisValue(chatRoomEntity.id!!, body)
        redisUtils.lpush(RedisUtils.CHAT_MESSAGES_KEY, value)

        val messageId = UUID.randomUUID().toString().substring(0, 4)
        log.info("Saved message in Redis: value=$value, messageId=$messageId")
        return ChatMessageResponse(body.message, body.userToken, messageId, roomKeyword)
    }

    fun markMessageAsRead(roomKeyword: String, messageId: String, userToken: String): Long{
        val key = createReadMessageRedisKey(messageId)
        redisUtils.saveWithSet(key, userToken)

        val totalParticipants = chatRoomService.getParticipantCount(roomKeyword)
        val readCount = getReadCount(messageId)
        val unreadCount = totalParticipants - readCount

        log.info("Message read update: messageId=$messageId, totalParticipants=$totalParticipants, readCount=$readCount, unreadCount=$unreadCount")
        return unreadCount
    }

    private fun createRedisValue(chatRoomId: Long, body: ChatMessageRequest): String{
        return chatRoomId.toString() + ":" + body.userToken + ":" + body.message
    }

    private fun getReadCount(messageId: String): Long {
        val key = createReadMessageRedisKey(messageId)
        return redisUtils.getSetSize(key)
    }

    private fun createReadMessageRedisKey(messageId: String): String = RedisUtils.CHAT_READ_PREFIX + messageId
}