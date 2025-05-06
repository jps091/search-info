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
@DataJdbcTest
@Transactional
class SearchInfoCommandRepositoryTest extends Specification {

    @Autowired
    JdbcTemplate jdbcTemplate

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate

    SearchInfoCommandRepository searchInfoCommandRepository

    @SpringBean
    NaverClient naverClient = Mock()

    @SpringBean
    KakaoClient kakaoClient = Mock()

    void setup(){
        searchInfoCommandRepository = new SearchInfoCommandRepository(jdbcTemplate, namedParameterJdbcTemplate)
    }

    def "저장이 된다."(){
        given:
        def givenQuery = "HTTP"

        when:
        def searchInfo = SearchInfo.create(givenQuery, LocalDateTime.now())
        def savedId = searchInfoCommandRepository.save(searchInfo)

        then:
        verifyAll {
            savedId != null
        }
    }

    def "2개 이상의 단어를 BatchQuery로 한번에 저장을 한다."(){
        given:
        def now = LocalDateTime.now()
        def searchInfoList = List.of(
                SearchInfo.create("HTTP", now),
                SearchInfo.create("JAVA", now),
                SearchInfo.create("SPRING", now)
        )

        when:
        searchInfoCommandRepository.saveAll(searchInfoList)

        then:
        def count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM search_info", Integer)
        count == 3

        def queries = jdbcTemplate.queryForList("SELECT query FROM search_info", String)
        queries.containsAll(["HTTP", "JAVA", "SPRING"])
    }

    def "지정된 ID들의 search_count가 증가된다"() {
        given:
        def now = LocalDateTime.now()
        def searchInfoList = List.of(
                SearchInfo.create("HTTP", now),
                SearchInfo.create("JAVA", now)
        )
        searchInfoCommandRepository.saveAll(searchInfoList)

        def ids = jdbcTemplate.queryForList("SELECT id FROM search_info", Integer)

        when:
        searchInfoCommandRepository.increaseSearchCount(ids)

        then:
        def counts = jdbcTemplate.queryForList("SELECT search_count FROM search_info", Integer)
        counts.every { it == 2 } // 초기값 1 + 1 증가
    }
}
