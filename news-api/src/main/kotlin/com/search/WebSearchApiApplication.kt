package com.search



import com.search.feign.KakaoClient
import com.search.feign.NaverClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync


@EnableFeignClients(clients = [NaverClient::class, KakaoClient::class])
@SpringBootApplication
@EnableAsync
class WebSearchApiApplication

fun main(args: Array<String>){
    runApplication<WebSearchApiApplication>(*args)
}