package com.search.domain.websearch.controller

import com.search.domain.websearch.controller.request.SearchRequest
import com.search.domain.websearch.infrastructure.result.WebSearchPageResult
import com.search.domain.websearch.infrastructure.result.WebSearchResult
import com.search.domain.websearch.service.WebQueryService
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

    def "정상인자로 search 요청시 성공한다."(){
        given:
        def request = new SearchRequest("HTTP", 1, 10)
        def mockPageQueryResult = new WebSearchPageResult<>(
                1, 10, 10,
                [new WebSearchResult("title1", "link1", "description1")]
        )

        and:
        1 * webQueryService.search(*_) >> mockPageQueryResult

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/webs")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath('$.totalElements').value(10))
                .andExpect(jsonPath('$.page').value(1))
                .andExpect(jsonPath('$.size').value(10))
                .andExpect(jsonPath('$.contents').isArray())
    }

    def "query가 비어있을때 BadRequest 응답반환된다."() {
        given:
        def request = new SearchRequest("", 1, 10)

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/webs")
                .param("query", request.query)
                .param("page", request.page.toString())
                .param("size", request.size.toString()))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.errorMessage').value("입력은 비어있을 수 없습니다."))
    }

    def "page가 음수일경우에 BadRequest 응답반환된다."(){
        given:
        def request = new SearchRequest("HTTP", -10, 10)

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
        def request = new SearchRequest("HTTP", 10, 51)

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
