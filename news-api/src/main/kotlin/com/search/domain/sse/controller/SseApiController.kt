package com.search.domain.sse.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.search.domain.sse.connection.SseConnectionPool
import com.search.domain.sse.connection.model.UserSseConnection
import com.search.domain.websearch.controller.response.TopRankResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.*

@RestController
@RequestMapping("/api/sse")
class SseApiController(
        private val sseConnectionPool: SseConnectionPool,
        private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @GetMapping(path = ["/connect"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun connect(): SseEmitter{
        val key = UUID.randomUUID().toString()
        log.info ("SSE 연결 시작, uniqueKey: $key")

        val connection = UserSseConnection.connect(
                key,
                sseConnectionPool,
                objectMapper
        )

        sseConnectionPool.addSession(key, connection)

        return connection.sseEmitter
    }

    @PostMapping("/push")
    fun pushRankingUpdate(
            @RequestBody rankingList: List<TopRankResponse>
    ) {
        log.info ("키워드 순위 업데이트 이벤트 푸시: $rankingList")
        sseConnectionPool.sendToAll("rankingUpdate", rankingList)
    }
}