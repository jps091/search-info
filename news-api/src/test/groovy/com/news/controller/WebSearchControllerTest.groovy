package com.news.controller

import com.news.search.controller.WebSearchController
import com.news.search.service.WebApplicationService
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

class WebSearchControllerTest extends Specification {
    WebApplicationService webApplicationService = Mock(WebApplicationService)
    WebSearchController webController
    MockMvc mockMvc

    void setup(){
        webController = new WebSearchController(webApplicationService)
        mockMvc = MockMvcBuilders.standaloneSetup(webController).build()
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

//    def "findStat"() {
//        given:
//        def givenQuery = "HTTP"
//        def givenDate = LocalDate.of(2024, 5, 1)
//
//        when:
//        def response = mockMvc.perform(
//                MockMvcRequestBuilders.get("/api/v1/webs/stats?query=${givenQuery}&date=${givenDate}"))
//                .andReturn()
//                .response
//
//        then:
//        response.status == HttpStatus.OK.value()
//
//        and:
//        1 * webApplicationService.findQueryCount(*_) >> {
//            String query, LocalDate date ->
//                assert query == givenQuery
//                assert date == givenDate
//        }
//    }


    def "findStatRanking"() {
        when:
        def response = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/webs/stats/ranking"))
                .andReturn()
                .response

        then:
        response.status == HttpStatus.OK.value()

        and:
        1 * webApplicationService.findTopQuery()
    }
}
