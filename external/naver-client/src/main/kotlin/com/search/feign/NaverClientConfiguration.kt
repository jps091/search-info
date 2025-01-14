package com.search.feign

import com.fasterxml.jackson.databind.ObjectMapper
import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class NaverClientConfiguration {

    @Bean
    fun requestInterceptor(
            @Value("\${external.naver.headers.client-id}") naverClientId: String,
            @Value("\${external.naver.headers.client-secret}") naverClientSecret: String
    ) : RequestInterceptor{
        return RequestInterceptor { requestTemplate ->
             requestTemplate.header("X-Naver-Client-Id", naverClientId)
             requestTemplate.header("X-Naver-Client-Secret", naverClientSecret)
        }
    }

    @Bean
    fun naverErrorDecoder(objectMapper: ObjectMapper) : NaverErrorDecoder{
        return NaverErrorDecoder(objectMapper)
    }
}