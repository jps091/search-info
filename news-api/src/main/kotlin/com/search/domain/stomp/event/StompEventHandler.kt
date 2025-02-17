package com.search.domain.stomp.event

import com.search.config.redis.RedisUtils
import com.search.domain.chatroom.service.ChatRoomService
import com.search.error.StompErrorType
import com.search.exception.ApiException
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
const val MAX_ROOM_SIZE = 3
@Component
class StompEventHandler(
        private val chatRoomService: ChatRoomService,
        private val redisUtils: RedisUtils
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        // 세션별 채팅방 매핑에 사용될 키 접두사
        const val SESSION_KEY_PREFIX = "SESSION:"
    }

    @EventListener
    fun handleConnectionEvent(event: SessionConnectEvent){
        val accessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = getSessionIdFrom(accessor)

        val roomKeywordHeaders = accessor.getNativeHeader("roomKeyword")
        if(roomKeywordHeaders.isNullOrEmpty()){
            log.info("roomKeyword가 없는 세션: $sessionId")
            throw ApiException("roomKeyword 에러", StompErrorType.ROOM_ID_NOT_EXIST, 500)
        }

        val roomKeyword = roomKeywordHeaders.first()
        verifyRoomSize(roomKeyword)
        participateChatRoom(sessionId, roomKeyword)
    }

    @EventListener
    fun handleDisconnectionEvent(event: SessionDisconnectEvent){
        val accessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = getSessionIdFrom(accessor)

        leaveChatRoom(sessionId)
    }

    private fun getSessionIdFrom(accessor: StompHeaderAccessor): String {
        val sessionId = accessor.sessionId ?: run {
            log.warn("StompHeaderAccessor에 세션 ID가 없습니다.")
            throw ApiException("세션 ID 에러", StompErrorType.SESSION_NOT_EXIST, 500)
        }
        return sessionId
    }

    private fun verifyRoomSize(roomKeyword: String) {
        val currentCount = chatRoomService.getParticipantCount(roomKeyword)
        if (currentCount >= MAX_ROOM_SIZE) {
            log.warn("채팅방 $roomKeyword 인원 초과 - 현재 인원: $currentCount")
            throw ApiException("인원수 에러", StompErrorType.ROOM_PEOPLE_EXCEED, 403)
        }
    }

    private fun participateChatRoom(sessionId: String, roomKeyword: String) {
        redisUtils.setValue(SESSION_KEY_PREFIX + sessionId, roomKeyword)
        chatRoomService.join(roomKeyword, sessionId)
        log.info("채팅방 $roomKeyword 자동 입장 - 세션 ID: $sessionId")
    }

    private fun leaveChatRoom(sessionId: String) {
        val roomKeyword = redisUtils.getValue(SESSION_KEY_PREFIX + sessionId)
        if (roomKeyword != null) {
            chatRoomService.leave(roomKeyword, sessionId)
            redisUtils.removeByKey(SESSION_KEY_PREFIX + sessionId)
            log.info("자동 퇴장 - 채팅방 $roomKeyword, 세션 ID: $sessionId")
        }
    }
}