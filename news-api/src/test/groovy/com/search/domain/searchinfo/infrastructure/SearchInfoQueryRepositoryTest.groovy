package com.search.domain.searchinfo.infrastructure

import com.search.domain.searchinfo.model.SearchInfo
import com.search.feign.KakaoClient
import com.search.feign.NaverClient
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import java.time.LocalDateTime

@ActiveProfiles("test")
@Transactional
@DataJdbcTest
class SearchInfoQueryRepositoryTest extends Specification {

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate

    SearchInfoQueryRepository searchInfoQueryRepository

    SearchInfoCommandRepository searchInfoCommandRepository

    @SpringBean
    NaverClient naverClient = Mock()

    @SpringBean
    KakaoClient kakaoClient = Mock()

    void setup() {
        searchInfoQueryRepository = new SearchInfoQueryRepository(jdbcTemplate, namedParameterJdbcTemplate)
        searchInfoCommandRepository = new SearchInfoCommandRepository(jdbcTemplate, namedParameterJdbcTemplate)

        def now = LocalDateTime.now()
        def data = List.of(
                SearchInfo.create("JAVA", now),
                SearchInfo.create("HTTP", now)
        )
        searchInfoCommandRepository.saveAll(data)
    }

    def "search_count가 높은 키워드부터 조회한다"(){
        given:
        def id = jdbcTemplate.queryForObject(
                "SELECT id FROM search_info WHERE query = ?",
                new Object[]{"HTTP"},
                Integer.class
        )
        searchInfoCommandRepository.increaseSearchCount(List.of(id))

        when:
        def results = searchInfoQueryRepository.findTopQuery()

        then:
        verifyAll {
            results[0].query == "HTTP"
            results[0].count == 2
            results[1].query == "JAVA"
            results[1].count == 1
        }
    }

    def "키워드 리스트를 전달하면, 이미 저장된 키워드만 반환한다"(){
        given:
        def keywordList = List.of("JAVA", "HTTP", "SPRING")

        when:
        def results = searchInfoQueryRepository.findByQueryList(keywordList)

        then:
        with(results){
            size() == 2
            results*.query.containsAll(["JAVA", "HTTP"])
        }
    }
}
