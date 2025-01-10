package com.news.controller.response

import com.news.search.controller.response.SearchResponse
import spock.lang.Specification

class SearchResponseTest extends Specification {
    def "searchResponse 객체가 생성된다."(){
        given:
        def title = "test news"
        def link = "www.test.com"
        def description = "test description"

        when:
        def result = SearchResponse.builder()
                .title(title)
                .link(link)
                .description(description)
                .build()

        then:
        verifyAll {
            result.title() == title
            result.link() == link
            result.description() == description
        }
    }
}
