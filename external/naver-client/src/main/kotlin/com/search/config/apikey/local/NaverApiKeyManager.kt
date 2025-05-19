package com.search.config.apikey.local

import com.github.benmanes.caffeine.cache.Cache
import com.search.config.apikey.NaverApiKeyManagerIfs
import com.search.config.apikey.NaverProperties
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("local", "prod", "test")
class NaverApiKeyManager(
        private val apiCallCache: Cache<String, Int>,
        private val naverProperties: NaverProperties // 설정값 주입
) : NaverApiKeyManagerIfs {
    private val log = LoggerFactory.getLogger(this::class.java)

    private var currentKeyIndex = 0
    private val lock = Any()

    override fun getCurrentApiKey(): NaverProperties.Header {
        return naverProperties.headers[currentKeyIndex]
    }

    override fun recordApiCall() {
        synchronized(lock) {
            val currentKey = getCurrentApiKey()
            val cacheKey = currentKey.clientId
            val currentCount = apiCallCache.getIfPresent(cacheKey) ?: 0

            if (currentCount >= 25000) {
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