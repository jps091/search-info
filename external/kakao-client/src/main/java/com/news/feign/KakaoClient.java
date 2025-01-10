package com.news.feign;

import com.news.model.KakaoWebResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoClient", url = "${external.kakao.url}", configuration = KakaoClientConfiguration.class)
public interface KakaoClient {
    @GetMapping("/v2/search/web")
    KakaoWebResponse search(@RequestParam("query") String query,
                            @RequestParam("page") int page,
                            @RequestParam("size") int size);
}