package com.search.domain.websearch.service

import com.search.domain.websearch.service.port.WebRepository
import spock.lang.Specification

class WebQueryServiceTest extends Specification {

    WebRepository naverWebRepository = Mock(WebRepository)
    WebRepository kakaoWebRepository = Mock(WebRepository)

    WebQueryService webQueryService

    void setup(){
        webQueryService = new WebQueryService(naverWebRepository, kakaoWebRepository)
    }

    def "query, page, size를 통해 검색을 할 수 있다."(){
        given:
        def givenQuery = "HTTP완벽가이드"
        def givenPage = 1
        def givenSize = 10

        when:
        webQueryService.search(givenQuery, givenPage, givenSize)

        then:
        1 * naverWebRepository.search(*_)>> {
            String query, int page, int size ->
                assert query == givenQuery
                assert page == givenPage
                assert size == givenSize
        }

        0 * kakaoWebRepository.search(*_)
    }
}
