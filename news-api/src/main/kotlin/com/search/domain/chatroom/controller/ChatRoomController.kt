package com.search.domain.chatroom.controller

import com.search.domain.chatroom.controller.response.ChatRoomResponse
import com.search.domain.chatroom.controller.response.ChatRoomsResponse
import com.search.domain.chatroom.controller.response.UserToken
import com.search.domain.chatroom.service.ChatRoomService
import com.search.domain.websearch.controller.request.SearchRequest
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/***
 * /api/v1/chat-room/${this.roomKeyword}/read
 * /api/v1/chat-room/history/${this.roomKeyword}
 * 1. 보내기
 * 2. 읽기(표시)
 * 3. 나가기
 */
@RestController
@RequestMapping("/api/v1/chat-rooms")
class ChatRoomController(
        private val chatRoomService: ChatRoomService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/token")
    fun issueToken(@Valid request: SearchRequest): UserToken{
        val token = UUID.randomUUID().toString()
        log.info("user token = $token")
        return UserToken(token)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun create(@RequestParam roomKeyword: String){
        chatRoomService.create(roomKeyword)
    }

    @ResponseBody
    @GetMapping
    fun retrieveAll(): List<ChatRoomResponse>{
        return chatRoomService.retrieveAll()
    }

    @ResponseBody
    @DeleteMapping("/{roomKeyword}")
    fun leave(@PathVariable roomKeyword: String,
              @RequestHeader("X-Session-Id") sessionId: String){
        chatRoomService.leave(roomKeyword, sessionId)
    }
}

/**
 *     @ResponseBody
 *     @PostMapping("/{roomKeyword}/read")
 *     fun read(@PathVariable roomKeyword: String,
 *              @RequestHeader("X-Session-Id") sessionId: String){
 *        chatRoomService.read(roomKeyword)
 *     }
 */