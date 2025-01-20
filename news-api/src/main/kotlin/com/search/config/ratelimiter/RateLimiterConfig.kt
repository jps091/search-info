package com.search.config.ratelimiter

import io.github.resilience4j.ratelimiter.RateLimiterConfig
import io.github.resilience4j.ratelimiter.RateLimiterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

//@Configuration
//class RateLimiterConfig {
//    @Bean
//    fun rateLimiterRegistry(): RateLimiterRegistry {
//        val config = RateLimiterConfig.custom()
//                .apply {
//                    limitForPeriod(10)
//                    limitRefreshPeriod(Duration.ofSeconds(1))
//                    timeoutDuration(Duration.ofMillis(500))
//                }.build()
//
//        return RateLimiterRegistry.of(config)
//    }
//}