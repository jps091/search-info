package com.search.config.caffeine

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.search.domain.websearch.controller.request.SummaryItem
import org.springframework.cache.annotation.EnableCaching
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Configuration("newsSummaryCacheConfig")
@EnableCaching
class CacheConfiguration {

    @Bean
    fun newsSummaryCache(): Cache<String, List<SummaryItem>>  {
        return Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(1)
            .build()
    }

    private fun getHoursUntilMidnight(): Long {
        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().atStartOfDay().plusDays(1)
        return Duration.between(now, midnight).toHours()
    }
}