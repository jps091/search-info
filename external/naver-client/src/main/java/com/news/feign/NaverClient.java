package com.news.feign;

import com.news.model.NaverWebResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naverClient", url = "${external.naver.url}", configuration = NaverClientConfiguration.class)
public interface NaverClient {
    @GetMapping("/v1/search/webkr.json")
    NaverWebResponse search(@RequestParam("query") String query,
                            @RequestParam("start") int start,
                            @RequestParam("display") int display);
}
