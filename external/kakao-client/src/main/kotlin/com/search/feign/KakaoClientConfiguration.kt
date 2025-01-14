package com.search.feign

import com.fasterxml.jackson.databind.ObjectMapper
import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class KakaoClientConfiguration {

    @Bean
    fun requestInterceptor(
            @Value("\${external.kakao.headers.rest-api-key}") restApiKey : String
    ) : RequestInterceptor{
        return RequestInterceptor { requestTemplate ->
            requestTemplate.header("Authorization", "KakaoAK " + restApiKey)
        }
    }

    @Bean
    fun kakaoErrorDecoder(
            objectMapper: ObjectMapper
    ) : KakaoErrorDecoder{
        return KakaoErrorDecoder(objectMapper)
    }
}