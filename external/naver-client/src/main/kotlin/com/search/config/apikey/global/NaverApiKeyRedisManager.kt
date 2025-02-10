package com.search.config.apikey.global

import com.search.config.apikey.NaverApiKeyManagerIfs
import com.search.config.apikey.NaverProperties
import com.search.config.cache.redis.RedisUtils
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
@Profile("global")
class NaverApiKeyRedisManager(
        private val redisUtils: RedisUtils,
        private val naverProperties: NaverProperties // 설정값 주입
) : NaverApiKeyManagerIfs {
    private val log = LoggerFactory.getLogger(this::class.java)

    private var currentKeyIndex = 0

    override fun getCurrentApiKey(): NaverProperties.Header {
        return naverProperties.headers[currentKeyIndex]
    }

    override fun recordApiCall() {
        val currentKey = getCurrentApiKey()
        val cacheKey = currentKey.clientId
        val lockKey = "lock:$cacheKey"

        while (!redisUtils.lock(lockKey)) {
            Thread.sleep(100)
        }

        try {
            // 락을 획득한 상태에서 API 호출 횟수를 읽어옴
            val currentCount = redisUtils.getValue(cacheKey).toInt()

            if (currentCount >= 25000) {
                // API 키 변경
                currentKeyIndex = (currentKeyIndex + 1) % naverProperties.headers.size
                log.info("API 키 변경됨: $currentKeyIndex (${currentKey.clientId})")
            } else {
                // 호출 횟수 증가
                redisUtils.increase(cacheKey)
                log.info("API 호출 횟수 증가: $currentCount -> ${currentCount + 1}")
            }
        } finally {
            // 반드시 락 해제
            redisUtils.unlock(lockKey)
        }
    }
}