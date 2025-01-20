package com.search.config.caffeine

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun apiCallCache(): Cache<String, Int> {
        return Caffeine.newBuilder()
                .expireAfterWrite(getHoursUntilMidnight(), TimeUnit.HOURS) // 자정까지 TTL 설정
                .maximumSize(10) // 최대 캐시 크기
                .build() // 제네릭 타입 명시적으로 선언
    }

    private fun getHoursUntilMidnight(): Long{
        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().atStartOfDay().plusDays(1)
        return Duration.between(now, midnight).toHours()
    }
}