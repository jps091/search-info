package com.search.feign

import com.search.model.KakaoWebResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
@FeignClient(name = "kakaoClient", url = "\${external.kakao.url}", configuration = [KakaoClientConfiguration::class])

interface KakaoClient {
    @GetMapping("/v2/search/web")
    fun search(
            @RequestParam("query") query : String,
            @RequestParam("page") page : Int,
            @RequestParam("size") size : Int
    ) : KakaoWebResponse
}