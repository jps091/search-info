package com.search.config.web.stomp

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class StompWebSocketConfig: WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws/connect")
                .setAllowedOrigins("http://localhost:3000", "https://search-info.n-e.kr")
                .withSockJS() // ws 통신을 http 엔드포인트로 사용할수 있게 허용
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        /***
         * /ws/public/1 형태로 메시지 발행 설정
         * /ws/public 시작하는 url 패턴으로 메시지가 발생 되면 컨트롤러의 @MessageMapping 메서드로 라우팅
         */
        registry.setApplicationDestinationPrefixes("/ws/publish")
        // /ws/topic/1 메시지 수진 설정
        registry.enableSimpleBroker("/ws/topic")
    }
}