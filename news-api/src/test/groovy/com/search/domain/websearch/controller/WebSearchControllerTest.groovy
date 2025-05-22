package com.search.domain.websearch.controller

import com.search.domain.websearch.service.WebApplicationService
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

class WebSearchControllerTest extends Specification {
    WebApplicationService webApplicationService = Mock(WebApplicationService)
    WebSearchController webSearchController
    MockMvc mockMvc

    void setup(){
        webSearchController = new WebSearchController(webApplicationService)
        mockMvc = MockMvcBuilders.standaloneSetup(webSearchController).build()
    }

    def "search"(){
        given:
        def givenQuery = "test news keyword"
        def givenPage = 1
        def givenSize = 10

        when:
        def response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/webs?query=${givenQuery}&size=${givenSize}&page=${givenPage}"))
                .andReturn()
                .response

        then:
        response.status == HttpStatus.OK.value()

        and:
        1 * webApplicationService.search(*_) >> {
            String query, int page, int size ->
                assert query == givenQuery
                assert page == givenPage
                assert size == givenSize
        }
    }

    def "findTopStats"(){
        given:
        when:
        def response = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/webs/ranking"))
                .andReturn()
                .response

        then:
        response.status == HttpStatus.OK.value()

        and:
        1 * webApplicationService.findTopQuery()
    }
}
