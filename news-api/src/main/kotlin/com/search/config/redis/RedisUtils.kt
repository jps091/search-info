package com.search.config.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class RedisUtils(
        private val redisTemplate: RedisTemplate<String, String>,
) {

    fun setValue(key: String, value: String) = redisTemplate.opsForSet().add(key, value)

    fun getSetSize(key: String): Long = redisTemplate.opsForSet().size(key) ?: 0

    fun getValue(key: String): String? = redisTemplate.opsForValue().get(key)

    fun getMembers(key: String): Set<String> = redisTemplate.opsForSet().members(key) ?: emptySet()

    fun removeBySet(key: String, value: String) =  redisTemplate.opsForSet().remove(key, value)

    fun removeByKey(key: String) = redisTemplate.delete(key)
}