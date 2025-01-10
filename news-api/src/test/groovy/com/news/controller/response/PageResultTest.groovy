package com.news.controller.response

import com.news.search.controller.response.PageResult
import com.news.search.controller.response.SearchResponse
import spock.lang.Specification

class PageResultTest extends Specification {
    def "pageResult 객체 생성된다."(){
        given:
        def page = 1
        def size = 10
        def totalElements = 5
        def searchResponse1 = GroovyMock(SearchResponse)
        def searchResponse2 = GroovyMock(SearchResponse)

        when:
        def result = new PageResult<>(page, size, totalElements, [searchResponse1, searchResponse2])

        then:
        verifyAll {
            result.page() == page
            result.size() == size
            result.totalElements() == totalElements
            result.contents().size() == 2
        }
    }
}
