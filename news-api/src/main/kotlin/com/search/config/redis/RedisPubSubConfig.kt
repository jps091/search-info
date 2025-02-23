package com.search.config.redis

import com.search.domain.stomp.service.ChatPubSubService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@Profile("global")
class RedisPubSubConfig(
        @Value("\${spring.data.redis.host}") private val host: String,
        @Value("\${spring.data.redis.port}") private val port: Int
) {
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(RedisStandaloneConfiguration(host, port))
    }

    @Bean
    @Primary
    fun redisStringTemplate(): RedisTemplate<String, String> {
        return createStringRedisTemplate()
    }

    private fun createStringRedisTemplate(): RedisTemplate<String, String> {
        return RedisTemplate<String, String>().apply {
            connectionFactory = redisConnectionFactory()
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = StringRedisSerializer()
            setDefaultSerializer(StringRedisSerializer())
        }
    }

    // Pub/Sub을 위한 StringRedisTemplate
    @Bean("chatPubSub")
    fun pubSubStringRedisTemplate(): StringRedisTemplate = StringRedisTemplate(redisConnectionFactory())

    /** Redis 메시지 리스너 컨테이너 (Pub/Sub 구독 설정) */
    @Bean
    fun redisMessageListenerContainer(
            redisConnectionFactory: RedisConnectionFactory,
            messageListenerAdapter: MessageListenerAdapter
    ):RedisMessageListenerContainer{
        return RedisMessageListenerContainer().apply {
            setConnectionFactory(redisConnectionFactory)
            addMessageListener(messageListenerAdapter, PatternTopic("chat")) // "chat" 채널 구독
        }
    }

    /** Redis에서 수신한 메시지를 처리할 MessageListenerAdapter */
    @Bean
    fun messageListenerAdapter(chatPubSubService: ChatPubSubService): MessageListenerAdapter {
        return MessageListenerAdapter(chatPubSubService, "onMessage")
    }
}