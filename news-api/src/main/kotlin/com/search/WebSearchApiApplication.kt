package com.search



import com.search.feign.KakaoClient
import com.search.feign.NaverClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling


@EnableFeignClients(clients = [NaverClient::class, KakaoClient::class])
@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableScheduling
class WebSearchApiApplication

fun main(args: Array<String>){
    SpringApplicationBuilder(WebSearchApiApplication::class.java)
            .profiles("local")
            .run(*args)
}