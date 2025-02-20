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


@Component
class StompEventHandler(
        private val chatRoomService: ChatRoomService,
        private val redisUtils: RedisUtils
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun handleConnectionEvent(event: SessionConnectEvent){
        val accessor = StompHeaderAccessor.wrap(event.message)
        val (roomKeyword, userToken) = extractRoomKeywordAndUserToken(accessor)

        if (accessor.sessionAttributes == null) {
            accessor.sessionAttributes = HashMap()
        }
        accessor.sessionAttributes!!["roomKeyword"] = roomKeyword
        accessor.sessionAttributes!!["userToken"] = userToken

        participateChatRoom(roomKeyword, userToken)
    }

    @EventListener
    fun handleDisconnectionEvent(event: SessionDisconnectEvent){
        val accessor = StompHeaderAccessor.wrap(event.message)

        val sessionAttributes = accessor.sessionAttributes
        val roomKeyword = sessionAttributes?.get("roomKeyword") as? String
        val userToken = sessionAttributes?.get("userToken") as? String

        if (roomKeyword == null || userToken == null) {
            log.error("세션 속성에서 roomKeyword 또는 userToken을 찾을 수 없습니다.")
            return
        }

        log.info("[$roomKeyword 채팅방 나가기 이벤트 발생] 유저 토큰 = $userToken")
        chatRoomService.leave(roomKeyword, userToken)
    }

    private fun extractRoomKeywordAndUserToken(accessor: StompHeaderAccessor): Pair<String, String> {
        val roomKeywordHeaders = accessor.getNativeHeader("roomKeyword")
        val userTokenHeaders = accessor.getNativeHeader("userToken")

        if (roomKeywordHeaders.isNullOrEmpty() || userTokenHeaders.isNullOrEmpty()) {
            throw ApiException("필수 헤더 누락", StompErrorType.HEADER_MISSING, 500)
        }

        val roomKeyword = roomKeywordHeaders.first()
        val userToken = userTokenHeaders.first()
        return Pair(roomKeyword, userToken)
    }

    private fun participateChatRoom(roomKeyword: String, userToken: String) {
        verifyRoomSize(roomKeyword, userToken)
        chatRoomService.join(roomKeyword, userToken)
        log.info("채팅방 $roomKeyword 자동 입장 - 사용자 토큰: $userToken")
    }

    private fun verifyRoomSize(roomKeyword: String, userToken: String) {
        if (isAlreadyParticipate(roomKeyword, userToken)) {
            log.info("채팅방 $roomKeyword 에 이미 참여 중인 사용자 토큰: $userToken")
            return
        }

        val currentCount = chatRoomService.getParticipantCount(roomKeyword)
        log.info("[$roomKeyword 채팅방 현재 참여자 수] = $currentCount")

        if (currentCount >= ChatRoomService.MAX_ROOM_SIZE) {
            log.error("채팅방 $roomKeyword 인원 초과 - 현재 인원: $currentCount")
            throw ApiException("인원수 에러", StompErrorType.ROOM_PEOPLE_EXCEED, 403)
        }
    }

    private fun isAlreadyParticipate(roomKeyword: String, userToken: String): Boolean{
        val redisKey = RedisUtils.CHAT_PARTICIPANTS_PREFIX + roomKeyword
        return redisUtils.getParticipantsFromHash(redisKey).contains(userToken)
    }
}
