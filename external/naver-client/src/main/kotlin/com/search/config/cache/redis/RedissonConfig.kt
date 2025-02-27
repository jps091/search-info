package com.search.config.cache.redis

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@Profile("global")
class RedissonConfig(
        @Value("\${spring.data.redis.host}") private val host: String,
        @Value("\${spring.data.redis.port}") private val port: Int
) {
    @Bean(destroyMethod = "shutdown")
    fun redissonClient(): RedissonClient{
        val config = Config().apply {
            useSingleServer().address = "redis://$host:$port"
        }
        return Redisson.create(config)
    }
}