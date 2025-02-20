package com.search.config.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisUtils(
        private val redisTemplate: RedisTemplate<String, String>,
) {
    companion object {
        const val CHAT_READ_PREFIX = "CHAT_READ:"
        const val CHAT_PARTICIPANTS_PREFIX = "CHAT_PARTICIPANTS:"
        const val CHAT_MESSAGES_KEY = "CHAT_MESSAGE"
        const val CHAT_PROCESSING_KEY = "CHAT_PROCESSING"
    }

    private val ttl: Duration = Duration.ofHours(24)

    fun saveWithSet(key: String, value: String) =
            redisTemplate.opsForSet().add(key, value).also {
                redisTemplate.expire(key, ttl)
            }

    fun lpush(key: String, value: String) =
            redisTemplate.opsForList().leftPush(key, value).also {
                redisTemplate.expire(key, ttl)
            }

    fun rpoplpush(sourceKey: String, destinationKey: String): String? =
            redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey).also {
                redisTemplate.expire(destinationKey, ttl)
            }

    fun lrem(key: String, count: Long, value: String) = redisTemplate.opsForList().remove(key, count, value)

    fun getValue(key: String): String? = redisTemplate.opsForValue().get(key)

    fun getSetSize(key: String): Long = redisTemplate.opsForSet().size(key) ?: 0

    fun getMembers(key: String): Set<String> = redisTemplate.opsForSet().members(key) ?: emptySet()

    fun removeBySet(key: String, value: String) =  redisTemplate.opsForSet().remove(key, value)

    /***
     *  다중 세션 관리를 위한 메서드
     */
    // 사용자 토큰의 세션 카운트를 증가 (없으면 생성)
    fun incrementHash(key: String, field: String): Long {
        val result = redisTemplate.opsForHash<String, String>().increment(key, field, 1)
        redisTemplate.expire(key, Duration.ofHours(1))
        return result
    }

    // 사용자 토큰의 세션 카운트를 감소
    fun decrementHash(key: String, field: String): Long =
            redisTemplate.opsForHash<String, String>().increment(key, field, -1)

    // 해당 Hash의 모든 필드와 값을 가져옴 (값은 문자열이므로 Long으로 변환)
    fun getHashFields(key: String): Map<String, Long> {
        val entries = redisTemplate.opsForHash<String, String>().entries(key)
        return entries.mapValues { it.value.toString().toLong() }
    }

    // Hash의 키(필드)를 고유 사용자 토큰으로 사용합니다.
    fun getParticipantsFromHash(key: String): Set<String> = getHashFields(key).keys

    // 해당 Hash 필드 삭제
    fun removeHashField(key: String, field: String) =
            redisTemplate.opsForHash<String, String>().delete(key, field)
}