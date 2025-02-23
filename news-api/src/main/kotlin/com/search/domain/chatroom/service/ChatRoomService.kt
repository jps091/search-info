package com.search.domain.chatroom.service

import com.search.config.redis.RedisUtils
import com.search.domain.chatroom.controller.response.ChatRoomResponse
import com.search.domain.chatroom.infrastructure.ChatRoomEntity
import com.search.domain.chatroom.infrastructure.ChatRoomJpaRepository
import com.search.domain.searchinfo.infrastructure.SearchInfoQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(
        private val redisUtils: RedisUtils,
        private val chatRoomJpaRepository: ChatRoomJpaRepository,
        private val searchInfoQueryRepository: SearchInfoQueryRepository,
        private val simpMessagingTemplate : SimpMessagingTemplate
) {
    companion object {
        const val MAX_ROOM_SIZE = 5
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun create(roomKeyword: String){
        if(!chatRoomJpaRepository.existsByRoomKeyword(roomKeyword)){
            log.info("[chat room create] = $roomKeyword")
            chatRoomJpaRepository.save(ChatRoomEntity(roomKeyword = roomKeyword))
        }
        log.info("[chat already exist] = $roomKeyword")
    }
    @Transactional(readOnly = true)
    fun retrieveAll(): List<ChatRoomResponse> {
        val topQueryResults = searchInfoQueryRepository.findTopQuery()
        log.info("retrieveAll -> topQueryResults $topQueryResults")

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

    fun join(roomKeyword: String, userToken: String){
        val redisKey = createRedisKey(roomKeyword)
        val count = redisUtils.incrementHash(redisKey, userToken)
        log.info("참여자 추가 - 채팅방: $roomKeyword, 유저 토큰: $userToken, 현재 인원: $count")
    }

    fun leave(roomKeyword: String, userToken: String){
        val redisKey = createRedisKey(roomKeyword)
        val count = redisUtils.decrementHash(redisKey, userToken)
        if (count <= 0) {
            redisUtils.removeHashField(redisKey, userToken)
        }
        log.info("참여자 제거 - 채팅방: $roomKeyword, 유저 토큰: $userToken, 남은 인원: $count")
        val updatedParticipants = redisUtils.getParticipantsFromHash(redisKey)
        simpMessagingTemplate.convertAndSend("/ws/topic/$roomKeyword/participants", updatedParticipants)
    }

    // 고유 사용자 수는 Hash의 필드 개수로 계산
    fun getParticipantCount(roomKeyword: String): Long {
        val redisKey = createRedisKey(roomKeyword)
        return redisUtils.getParticipantsFromHash(redisKey).size.toLong()
    }

    fun getParticipants(roomKeyword: String): Set<String> {
        val redisKey = createRedisKey(roomKeyword)
        return redisUtils.getParticipantsFromHash(redisKey)
    }

    private fun createRedisKey(roomKeyword: String): String {
        return RedisUtils.CHAT_PARTICIPANTS_PREFIX + roomKeyword
    }
}