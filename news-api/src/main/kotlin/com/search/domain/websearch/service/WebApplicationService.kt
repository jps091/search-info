package com.search.domain.websearch.service

import com.github.benmanes.caffeine.cache.Cache
import com.search.domain.searchinfo.infrastructure.SearchInfoQueryRepository
import com.search.domain.websearch.controller.response.PageSearchResponse
import com.search.domain.websearch.controller.response.SearchResponse
import com.search.domain.websearch.infrastructure.result.WebSearchPageResult
import com.search.domain.websearch.infrastructure.result.WebSearchResult
import com.search.domain.searchinfo.infrastructure.result.TopQueryResult
import com.search.domain.websearch.controller.request.SummaryItem
import com.search.domain.websearch.controller.request.SummaryData
import com.search.domain.websearch.controller.response.TopRankResponse
import com.search.domain.websearch.event.EventRequest
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDateTime



@Service
class WebApplicationService(
        val webQueryService: WebQueryService,
        val searchInfoQueryRepository: SearchInfoQueryRepository,
        val eventPublisher: ApplicationEventPublisher,
        val newsSummaryCache: Cache<String, List<SummaryItem>>
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun search(query: String, page: Int, size: Int): PageSearchResponse<SearchResponse> {
        val result = webQueryService.search(query, page, size)
        if(isNotEmptyResult(result)){
            log.info("검색결과 개수: {}", result.size);
            eventPublisher.publishEvent(EventRequest(query, LocalDateTime.now()))
        }
        return convertToPageResponse(result)
    }

    fun findTopQuery(): List<TopRankResponse>{
        val result = searchInfoQueryRepository.findTopQuery()
        return result.map{ toTopRankResponse(it) }
    }

    fun saveSummaryNews(news: SummaryData){
        newsSummaryCache.put("today", news.results)
    }

    fun getTodaySummary(): SummaryData {
        val summaryItems = newsSummaryCache.getIfPresent("today") ?: emptyList()
        return SummaryData(summaryItems)
    }

    private fun isNotEmptyResult(result: WebSearchPageResult<WebSearchResult>): Boolean{
        return result.contents.isNotEmpty()
    }

    private fun convertToPageResponse(pageResult: WebSearchPageResult<WebSearchResult>): PageSearchResponse<SearchResponse> {
        val result = pageResult.contents.map { toSearchResponse(it) }
        return PageSearchResponse(
                page = pageResult.page,
                size = pageResult.size,
                totalElements = pageResult.totalElements,
                contents = result
        )
    }

    private fun toSearchResponse(result: WebSearchResult): SearchResponse {
        return SearchResponse(
                title = result.title,
                link = result.link,
                description =  result.description
        )
    }

    private fun toTopRankResponse(result: TopQueryResult): TopRankResponse {
        return TopRankResponse(
                query = result.query,
                count = result.count
        )
    }
}

