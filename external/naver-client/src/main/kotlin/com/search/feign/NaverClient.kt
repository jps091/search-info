package com.search.feign

import com.search.config.feign.NaverClientConfiguration
import com.search.model.NaverWebResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "naverClient", url = "\${external.naver.url}", configuration = [NaverClientConfiguration::class])
interface NaverClient {
    @GetMapping("/v1/search/webkr.json")
    fun search(
            @RequestParam("query") query: String,
            @RequestParam("start") start: Int,
            @RequestParam("display") display: Int
    ) : NaverWebResponse
}