package com.search.config.apikey

import com.github.benmanes.caffeine.cache.Cache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class NaverApiKeyManager(
        private val apiCallCache: Cache<String, Int>,
        private val naverProperties: NaverProperties // 설정값 주입
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private var currentKeyIndex = 0
    private val lock = Any()

    fun getCurrentApiKey(): NaverProperties.Header {
        return naverProperties.headers[currentKeyIndex]
    }

    fun recordApiCall() {
        synchronized(lock) {
            val currentKey = getCurrentApiKey()
            val cacheKey = currentKey.clientId
            val currentCount = apiCallCache.getIfPresent(cacheKey) ?: 0

            if (currentCount >= 2) {
                // API 키 변경
                currentKeyIndex = (currentKeyIndex + 1) % naverProperties.headers.size
                log.info("API 키 변경됨: $currentKeyIndex (${currentKey.clientId})")
            } else {
                // 호출 횟수 증가
                apiCallCache.put(cacheKey, currentCount + 1)
                log.info("API 호출 횟수 증가: $currentCount -> ${currentCount + 1}")
            }
        }
    }
}