package com.search.config.redis

import org.redisson.Redisson
import org.redisson.api.RTopic
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile


@Configuration
@Profile("global")
class RedissonPubSubConfig(
        @Value("\${spring.data.redis.host}") private val host: String,
        @Value("\${spring.data.redis.port}") private val port: Int
) {
    @Bean(destroyMethod = "shutdown")
    fun redissonClient(): RedissonClient {
        val config = Config().apply {
            // 단일 서버 모드 설정 (cluster나 sentinel 환경이면 다르게 설정)
            useSingleServer().address = "redis://$host:$port"
        }
        return Redisson.create(config)
    }

    // "chat" 채널을 위한 RTopic 빈 등록
    @Bean
    fun redissonChatTopic(redissonClient: RedissonClient): RTopic {
        return redissonClient.getTopic("chat")
    }
}