package com.search.config.apikey

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "external.naver")
class NaverProperties {
    lateinit var url: String
    lateinit var headers: List<Header> // client-id와 client-secret의 리스트

    data class Header(
            val clientId: String,
            val clientSecret: String
    )
}