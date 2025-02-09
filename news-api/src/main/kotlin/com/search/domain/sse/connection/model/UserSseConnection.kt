package com.search.domain.sse.connection.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.search.domain.sse.connection.ConnectionPoolIfs
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

/***
 * SSE 연결을 캡슐화한 클래스로, 생성 시 SseEmitter를 만들고
 * onCompletion/timeout 콜백을 등록하며 초기 “onopen” 이벤트를 전송합니다.
 */
class UserSseConnection(
        val uniqueKey: String,
        private val connectionPoolIfs: ConnectionPoolIfs<String, UserSseConnection>,
        private val objectMapper: ObjectMapper
) {
    val sseEmitter: SseEmitter = SseEmitter(60 * 1000L)

    init {
        sseEmitter.onCompletion{
            connectionPoolIfs.onCompletionCallback(this)
        }

        sseEmitter.onTimeout{
            sseEmitter.complete()
        }

        sendMessage("onopen", "connect")
    }

    fun sendMessage(eventName: String, data: Any){
        try{
            val json = objectMapper.writeValueAsString(data)
            val event = SseEmitter.event()
                    .name(eventName)
                    .data(json)

            sseEmitter.send(event)
        }catch (e: Exception){
            sseEmitter.completeWithError(e)
        }
    }

    fun sendMessage(data: Any) {
        try {
            val json = objectMapper.writeValueAsString(data)
            val event = SseEmitter.event()
                    .data(json)

            sseEmitter.send(event)
        } catch (e: Exception) {
            sseEmitter.completeWithError(e)
        }
    }

    companion object {
        fun connect(
                uniqueKey: String,
                connectionPoolIfs: ConnectionPoolIfs<String, UserSseConnection>,
                objectMapper: ObjectMapper
        ): UserSseConnection {
            return UserSseConnection(uniqueKey, connectionPoolIfs, objectMapper)
        }
    }
}