package com.news.service

import com.news.search.service.WebQueryService
import com.news.search.service.port.WebRepository
import spock.lang.Specification

class WebQueryServiceTest extends Specification {
    WebRepository naverWebRepository = Mock(WebRepository)
    WebRepository kakaoWebRepository = Mock(WebRepository)
    WebQueryService webQueryService

    void setup(){
        webQueryService = new WebQueryService(naverWebRepository, kakaoWebRepository)
    }

    def "search시 인자가 그대로 넘어가고 naver쪽으로 호출한다."() {
        given:
        def givenQuery = "HTTP완벽가이드"
        def givenPage = 1
        def givenSize = 10

        when:
        webQueryService.search(givenQuery, givenPage, givenSize)

        then:
        1 * naverWebRepository.search(*_) >> {
            String query, int page, int size ->
                assert query == givenQuery
                assert page == givenPage
                assert size == givenSize
        }

        and:
        0 * kakaoWebRepository.search(*_)
    }
}
