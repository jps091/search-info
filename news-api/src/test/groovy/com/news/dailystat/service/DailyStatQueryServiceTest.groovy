package com.news.dailystat.service

import com.news.dailystat.infrastructure.DailyStatJdbcRepository
import com.news.dailystat.infrastructure.DailyStatJpaRepository
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class DailyStatQueryServiceTest extends Specification {

    DailyStatQueryService dailyStatQueryService

    DailyStatJdbcRepository dailyStatRepository = Mock(DailyStatJdbcRepository)
    DailyStatJpaRepository dailyStatJpaRepository = Mock(DailyStatJpaRepository)

    void setup() {
        dailyStatQueryService = new DailyStatQueryService(dailyStatRepository, dailyStatJpaRepository)
    }

    def "findQueryCount 조회시 한달치를 조회하면서 쿼리개수가 반환된다."() {
        given:
        def givenQuery = 'HTTP'
        def givenDate = LocalDate.of(2025, 1, 1)
        def expectedCount = 2

        when:
        def response = dailyStatQueryService.findQueryCount(givenQuery, givenDate)

        then:
        1 * dailyStatRepository.countByQueryAndEventDateTimeBetween(
                givenQuery,
                LocalDateTime.of(2025, 1, 1, 0, 0,0),
                LocalDateTime.of(2025, 1, 31, 23, 59,59, 999999999),
        ) >> expectedCount

        and:
        response.count() == expectedCount
    }

    def "findTop15Query 조회시 상위 5개 반환 요청이 들어간다."() {
        when:
        dailyStatQueryService.findTop15Query()

        then:
        1 * dailyStatRepository.findTopQuery(*_) >> { int limit ->
            assert limit == 15
        }
    }
}
