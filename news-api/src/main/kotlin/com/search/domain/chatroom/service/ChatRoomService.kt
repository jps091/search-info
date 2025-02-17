package com.search.domain.chatroom.service

import com.search.config.redis.RedisUtils
import com.search.domain.chatroom.controller.response.ChatRoomResponse
import com.search.domain.chatroom.controller.response.ChatRoomsResponse
import com.search.domain.chatroom.infrastructure.ChatRoomEntity
import com.search.domain.chatroom.infrastructure.ChatRoomJpaRepository
import com.search.domain.searchinfo.infrastructure.SearchInfoQueryRepository
import com.search.domain.stomp.event.MAX_ROOM_SIZE
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.query.JpaQueryCreator
import org.springframework.stereotype.Service

@Service
class ChatRoomService(
        private val redisUtils: RedisUtils,
        private val chatRoomJpaRepository: ChatRoomJpaRepository,
        private val searchInfoQueryRepository: SearchInfoQueryRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        // 각 채팅방의 키 접두사
        const val CHAT_ROOM_PREFIX = "CHAT_ROOM:"
    }

    fun create(roomKeyword: String){
        if(!chatRoomJpaRepository.existsByRoomKeyword(roomKeyword)){
            log.info("[chat room create] = $roomKeyword")
            chatRoomJpaRepository.save(ChatRoomEntity(roomKeyword))
        }
        log.info("[chat already exist] = $roomKeyword")
    }

    fun retrieveAll(): List<ChatRoomResponse> {
        val topQueryResults = searchInfoQueryRepository.findTopQuery()

        return topQueryResults.mapNotNull { topQueryResult ->
            chatRoomJpaRepository.findByRoomKeyword(topQueryResult.query)?.let { chatRoomEntity ->
                ChatRoomResponse(
                        chatRoomEntity.roomKeyword,
                        getParticipants(chatRoomEntity.roomKeyword),
                        MAX_ROOM_SIZE
                )
            }
        }
    }

    fun join(roomKeyword: String, sessionId: String){
        val redisKey = createRedisKey(roomKeyword)
        redisUtils.setValue(redisKey, sessionId)
        val count = redisUtils.getSetSize(redisKey)
        log.info("참여자 추가 - 채팅방: $roomKeyword, 세션: $sessionId, 현재 인원: $count")
    }

    fun leave(roomKeyword: String, sessionId: String){
        val redisKey = createRedisKey(roomKeyword)
        redisUtils.removeBySet(redisKey, sessionId)
        val count = redisUtils.getSetSize(redisKey)
        log.info("참여자 제거 - 채팅방: $roomKeyword, 세션: $sessionId, 남은 인원: $count")
    }

    fun getParticipantCount(roomKeyword: String): Long{
        val redisKey = createRedisKey(roomKeyword)
        return redisUtils.getSetSize(redisKey)
    }

    fun getParticipants(roomKeyword: String): Set<String> {
        val redisKey = createRedisKey(roomKeyword)
        return redisUtils.getMembers(redisKey)
    }

    private fun createRedisKey(roomKeyword: String): String {
        val redisKey = CHAT_ROOM_PREFIX + roomKeyword
        return redisKey
    }
}