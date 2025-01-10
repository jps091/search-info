package com.news;

import com.news.feign.KakaoClient;
import com.news.feign.NaverClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableFeignClients(clients = {NaverClient.class, KakaoClient.class})
@SpringBootApplication
@EnableAsync
public class WebSearchApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebSearchApiApplication.class, args);
    }
}
