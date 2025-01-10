package com.news.search.service

import com.news.dailystat.service.DailyStatQueryService
import com.news.dailystat.service.response.DailyStatQueryResponse
import com.news.search.service.event.SearchEvent
import com.news.search.service.response.PageQueryResult
import com.news.search.service.response.SearchQueryResponse
import com.news.searchinfo.infrastructure.SearchInfoJdbcQueryRepository
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification

import java.time.LocalDate

class WebApplicationServiceTest extends Specification {
    WebApplicationService webApplicationService

    WebQueryService webQueryService = Mock(WebQueryService)
    SearchInfoJdbcQueryRepository searchInfoJdbcQueryRepository = Mock(SearchInfoJdbcQueryRepository)
    ApplicationEventPublisher eventPublisher = Mock(ApplicationEventPublisher)

    void setup() {
        webApplicationService = new WebApplicationService(webQueryService, searchInfoJdbcQueryRepository, eventPublisher)
    }

    def "search메서드 호출시 검색결과를 반환하면서 통계데이터를 저장한다."() {
        given:
        def givenQuery = "HTTP"
        def givenPage = 1
        def givenSize = 10
        def mockPageQueryResult = new PageQueryResult<>(
                1, 10, 100,
                [new SearchQueryResponse("title1", "link1", "description1")]
        )

        when:
        webApplicationService.search(givenQuery, givenPage, givenSize)


        then:
        1 * webQueryService.search(*_) >> {
            String query, int page, int size ->
                assert query == givenQuery
                assert page == givenPage
                assert size == givenSize
                return mockPageQueryResult
        }
        and: "저장 이벤트를 발행한다."
        1 * eventPublisher.publishEvent(_ as SearchEvent)
    }

//    def "findQueryCount메서드 호출시 인자를 그대로 넘긴다"() {
//        given:
//        def givenQuery = 'HTTP'
//        def givenDate = LocalDate.of(2024, 5, 1)
//        def givenResponse = new DailyStatQueryResponse('HTTP', 10)
//
//        when:
//        webApplicationService.findQueryCount(givenQuery, givenDate)
//
//        then:
//        1 * dailyStatQueryService.findQueryCount(*_) >> {
//            String query, LocalDate date ->
//                assert query == givenQuery
//                assert date == givenDate
//                return givenResponse
//        }
//    }

    def "findTop5Query메서드 호출시 dailyStatQueryService의 findTop5Query가 호출된다."() {
        given:
        when:
        searchInfoJdbcQueryRepository.findTopQuery(10)

        then:
        1 * searchInfoJdbcQueryRepository.findTopQuery(10)
    }
}
