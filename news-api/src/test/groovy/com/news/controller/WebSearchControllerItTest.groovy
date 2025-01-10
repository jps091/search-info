package com.news.controller

import com.news.search.controller.request.SearchRequest
import com.news.search.controller.response.PageResult
import com.news.search.controller.response.SearchResponse
import com.news.search.service.WebQueryService
import com.news.search.service.response.PageQueryResult
import com.news.search.service.response.SearchQueryResponse
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class WebSearchControllerItTest extends Specification {
    @Autowired
    MockMvc mockMvc

    @SpringBean
    WebQueryService webQueryService = Mock()

    def "정상인자로 요청시 성공한다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: 1, size: 10)
        def mockPageQueryResult = new PageQueryResult<>(
                1, 10, 10,
                [new SearchQueryResponse("title1", "link1", "description1")]
        )

        and:
        1 * webQueryService.search(*_) >> mockPageQueryResult

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/webs")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result//.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.totalElements').value(10))
                .andExpect(jsonPath('$.page').value(1))
                .andExpect(jsonPath('$.size').value(10))
                .andExpect(jsonPath('$.contents').isArray())
    }

    def "query가 비어있을때 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest(query: "", page: 1, size: 10)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/webs")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("입력은 비어있을 수 없습니다."))
    }

    def "page가 음수일경우에 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: -10, size: 10)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/webs")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("페이지번호는 1이상이어야 합니다."))
    }

    def "size가 50을 초과하면 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest(query: "HTTP", page: 1, size: 51)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/webs")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("페이지크기는 50이하여야 합니다."))
    }
}
