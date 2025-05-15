package com.search.domain.websearch.service

import com.search.domain.searchinfo.infrastructure.SearchInfoQueryRepository
import com.search.domain.searchinfo.infrastructure.result.TopQueryResult
import com.search.domain.websearch.controller.response.PageSearchResponse
import com.search.domain.websearch.controller.response.SearchResponse
import com.search.domain.websearch.event.EventRequest
import com.search.domain.websearch.infrastructure.result.WebSearchPageResult
import com.search.domain.websearch.infrastructure.result.WebSearchResult
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification

class WebApplicationServiceTest extends Specification {

    WebApplicationService webApplicationService

    WebQueryService webQueryService = Mock(WebQueryService)
    SearchInfoQueryRepository searchInfoQueryRepository = Mock(SearchInfoQueryRepository)
    ApplicationEventPublisher eventPublisher = Mock(ApplicationEventPublisher)

    void setup(){
        webApplicationService = new WebApplicationService(webQueryService, searchInfoQueryRepository, eventPublisher)
    }

    def "검색을 하면 PageSearchResponse를 결과로 반환하고, 이벤트 처리를 통해 키워드를 저장한다"(){
        given:
        def givenQuery = "HTTP"
        def givenPage = 1
        def givenSize = 10
        def mockWebSearchPageResult = new WebSearchPageResult<>(
                1, 10, 100,
                [new WebSearchResult("title1", "link1", "description1") ]
        )
        def mockPageQueryResult = new PageSearchResponse<>(
                1, 10, 100,
                [new SearchResponse("title1", "link1", "description1")]
        )

        webQueryService.search(givenQuery, givenPage, givenSize) >> mockWebSearchPageResult

        when:
        def result = webApplicationService.search(givenQuery, givenPage, givenSize)

        then:
        result == mockPageQueryResult

        and: "result가 존재한다면 이벤트 발생"
        1 * eventPublisher.publishEvent({ ev ->
            ev instanceof EventRequest && ev.query == givenQuery
        })
    }

    def "검색 결과가 empty이면 이벤트를 발행하지 않는다"(){
        given:
        def givenQuery = "HTTP"
        def givenPage = 1
        def givenSize = 10
        def mockWebSearchPageResult = new WebSearchPageResult<>(
                1, 10, 0, []
        )
        webQueryService.search(givenQuery, givenPage, givenSize) >> mockWebSearchPageResult

        when:
        webApplicationService.search(givenQuery, givenPage, givenSize)

        then:
        0 * eventPublisher.publishEvent(_)
    }

    def "findTopQuery는 searchInfoQueryRepository 조회결과를 가지고 List<TopRankResponse>를 반환한다"(){
        given:
        def raw1 = new TopQueryResult("JAVA", 5)
        def raw2 = new TopQueryResult("HTTP", 4)
        def mockRaws = List.of(raw1, raw2)

        searchInfoQueryRepository.findTopQuery() >> mockRaws

        when:
        def results = webApplicationService.findTopQuery()

        then:
        with(results){
            results*.query == ["JAVA", "HTTP"]
            results*.count == [5, 4]
        }
    }
}
