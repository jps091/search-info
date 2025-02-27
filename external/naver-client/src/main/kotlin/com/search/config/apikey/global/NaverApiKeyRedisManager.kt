package com.search.config.apikey.global

import com.search.config.apikey.NaverApiKeyManagerIfs
import com.search.config.apikey.NaverProperties
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
@Profile("global")
class NaverApiKeyRedisManager(
        private val redissonClient: RedissonClient,
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

        val lock = redissonClient.getLock(lockKey)

        try {
            val available = lock.tryLock(3, 2, TimeUnit.SECONDS)
            if(!available){
                log.info("$lockKey 락 획득 실패")
                return
            }

            // RAtomicLong을 사용하여 API 호출 횟수를 원자적으로 관리
            val counter = redissonClient.getAtomicLong(cacheKey)
            val currentCount = counter.get()

            if (currentCount >= 25000) {
                // API 키 변경
                currentKeyIndex = (currentKeyIndex + 1) % naverProperties.headers.size
                log.info("API 키 변경됨: $currentKeyIndex (${currentKey.clientId})")
            } else {
                // 호출 횟수 증가
                val newCount = counter.incrementAndGet()
                log.info("API 호출 횟수 증가: $currentCount -> $newCount")
            }
        } finally {
            // 반드시 락 해제
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }
}