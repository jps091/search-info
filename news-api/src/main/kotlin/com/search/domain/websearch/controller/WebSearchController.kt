package com.search.domain.websearch.controller

import com.search.domain.websearch.controller.request.SearchRequest
import com.search.domain.websearch.controller.response.PageSearchResponse
import com.search.domain.websearch.controller.response.SearchResponse
import com.search.domain.websearch.controller.response.TopRankResponse
import com.search.domain.websearch.service.WebApplicationService
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
@RestController
@RequestMapping("/api/v1/webs")
class WebSearchController(
        private val webApplicationService: WebApplicationService
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    @ResponseBody
    @GetMapping
    @RateLimiter(name = "apiRateLimiter")
    fun search(@Valid request: SearchRequest): PageSearchResponse<SearchResponse> {
        return webApplicationService.search(request.query, request.page, request.size)
    }

    @ResponseBody
    @GetMapping("/ranking")
    @RateLimiter(name = "apiRateLimiter")
    fun findTopStats(): List<TopRankResponse>{
        //log.info("webApplicationService.findTopQuery={}", webApplicationService.findTopQuery())
        return webApplicationService.findTopQuery()
    }
}

