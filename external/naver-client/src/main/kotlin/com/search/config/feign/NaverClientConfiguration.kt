package com.search.config.feign

import com.fasterxml.jackson.databind.ObjectMapper
import com.search.config.apikey.NaverApiKeyManagerIfs
import com.search.config.apikey.local.NaverApiKeyManager
import com.search.feign.NaverErrorDecoder
import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NaverClientConfiguration(
        private val naverApiKeyManager: NaverApiKeyManagerIfs
) {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { requestTemplate ->
            val currentApiKey = naverApiKeyManager.getCurrentApiKey()
            requestTemplate.header("X-Naver-Client-Id", currentApiKey.clientId)
            requestTemplate.header("X-Naver-Client-Secret", currentApiKey.clientSecret)
            naverApiKeyManager.recordApiCall() // 호출 횟수 기록
        }
    }

    @Bean
    fun naverErrorDecoder(objectMapper: ObjectMapper) : NaverErrorDecoder {
        return NaverErrorDecoder(objectMapper)
    }
}