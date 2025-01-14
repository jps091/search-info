package com.search.websearch.controller

import com.search.websearch.controller.request.SearchRequest
import com.search.websearch.controller.response.PageSearchResponse
import com.search.websearch.controller.response.SearchResponse
import com.search.websearch.controller.response.TopRankResponse
import com.search.websearch.service.WebApplicationService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
@RestController
@RequestMapping("/api/v1/webs")
class WebSearchController(
        private val webApplicationService: WebApplicationService
) {

    private val log = LoggerFactory.getLogger(this::class.java)
    @GetMapping
    @ResponseBody
    fun search(@Valid request: SearchRequest): PageSearchResponse<SearchResponse>{
        return webApplicationService.search(request.query, request.page, request.size)
    }

    @GetMapping("/stats/ranking")
    @ResponseBody
    fun findTopStats(): List<TopRankResponse>{
        log.info("webApplicationService.findTopQuery={}", webApplicationService.findTopQuery())
        return webApplicationService.findTopQuery()
    }
}

