package com.search.websearch.service

import com.news.search.service.event.SearchEvent
import com.search.searchinfo.infrastructure.SearchInfoQueryRepository
import com.search.websearch.controller.model.PageSearchResponse
import com.search.websearch.controller.model.SearchResponse
import com.search.websearch.infrastructure.result.WebSearchPageResult
import com.search.websearch.infrastructure.result.WebSearchResult
import com.search.searchinfo.infrastructure.result.TopQueryResult
import com.search.websearch.controller.model.TopRankResponse
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private const val QUERY_SIZE = 15

@Service
class WebApplicationService(
        val webQueryService: WebQueryService,
        val searchInfoQueryRepository: SearchInfoQueryRepository,
        val eventPublisher: ApplicationEventPublisher
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun search(query: String, page: Int, size: Int): PageSearchResponse<SearchResponse>{
        val result = webQueryService.search(query, page, size)
        if(isNotEmptyResult(result)){
            log.info("검색결과 개수: {}", result.size);
            eventPublisher.publishEvent(SearchEvent(query, LocalDateTime.now()))
        }
        return convertToPageResponse(result)
    }

    fun findTopQuery(): List<TopRankResponse>{
        val result = searchInfoQueryRepository.findTopQuery(QUERY_SIZE)
        return result.map{ toTopRankResponse(it) }
    }

    private fun isNotEmptyResult(result: WebSearchPageResult<WebSearchResult>): Boolean{
        return result.contents.isNotEmpty()
    }

    private fun convertToPageResponse(pageResult: WebSearchPageResult<WebSearchResult>): PageSearchResponse<SearchResponse>{
        val result = pageResult.contents.map { toSearchResponse(it) }
        return PageSearchResponse(
                page = pageResult.page,
                size = pageResult.size,
                totalElements = pageResult.totalElements,
                contents = result
        )
    }

    private fun toSearchResponse(result: WebSearchResult): SearchResponse{
        return SearchResponse(
                title = result.title,
                link = result.link,
                description =  result.description
        )
    }

    private fun toTopRankResponse(result: TopQueryResult): TopRankResponse{
        return TopRankResponse(
                query = result.query,
                searchCount = result.searchCount
        )
    }
}

