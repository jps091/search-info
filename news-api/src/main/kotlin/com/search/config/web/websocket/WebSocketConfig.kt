package com.search.config.web.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

//@Configuration
//@EnableWebSocket
//class WebSocketConfig(
//        private val myWebSocketHandler: MyWebSocketHandler
//): WebSocketConfigurer{
//    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
//        registry.addHandler(myWebSocketHandler, "/connect")
//    }
//}