package com.news.search.controller;

import com.news.search.controller.request.SearchRequest;
import com.news.search.controller.response.PageResult;
import com.news.search.controller.response.SearchResponse;
import com.news.search.controller.response.StatResponse;
import com.news.search.service.WebApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/webs")
public class WebSearchController {
    private final WebApplicationService webApplicationService;

    @GetMapping
    @ResponseBody
    public PageResult<SearchResponse> search(@Valid SearchRequest request){
        return webApplicationService.search(request.getQuery(), request.getPage(), request.getSize());
    }

    @GetMapping("/stats/ranking")
    @ResponseBody
    public List<StatResponse> findTopStats() {
        log.info("[WebSearchController] find top stats");
        return webApplicationService.findTopQuery();
    }
}
