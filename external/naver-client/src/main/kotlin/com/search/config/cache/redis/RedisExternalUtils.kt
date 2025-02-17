package com.search.config.cache.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisExternalUtils(
        private val redisTemplate: RedisTemplate<String, String>,
) {
    fun getValue(key: String): String = redisTemplate.opsForValue().get(key)!!

    fun increase(key: String) = redisTemplate.opsForValue().increment(key, 1)

    fun lock(key: String): Boolean {
        return redisTemplate.opsForValue().setIfAbsent(key, "token", Duration.ofMillis(3000))!!
    }

    fun unlock(key: String): Boolean {
        return redisTemplate.delete(key)
    }
}