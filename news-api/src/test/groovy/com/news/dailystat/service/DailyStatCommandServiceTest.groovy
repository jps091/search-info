package com.news.dailystat.service

import com.news.dailystat.infrastructure.DailyStatJdbcRepository
import com.news.dailystat.infrastructure.DailyStatJpaRepository
import com.news.dailystat.model.DailyStat
import spock.lang.Specification

import java.time.LocalDateTime

class DailyStatCommandServiceTest extends Specification {

    DailyStatCommandService dailyStatCommandService

    DailyStatJdbcRepository dailyStatRepository = Mock(DailyStatJdbcRepository)
    DailyStatJpaRepository dailyStatJpaRepository = Mock(DailyStatJpaRepository)

    void setup() {
        dailyStatCommandService = new DailyStatCommandService(dailyStatRepository, dailyStatJpaRepository)
    }

    def "저장시 넘어온 인자 그대로 호출된다."() {
        given:
        def givenDailyStat = DailyStat.create("HTTP", LocalDateTime.now())

        when:
        dailyStatCommandService.save(givenDailyStat)

        then:
        1 * dailyStatRepository.save(*_) >> {
            DailyStat dailyStat ->
                assert dailyStat.query == givenDailyStat.query
                assert dailyStat.eventDateTime == givenDailyStat.eventDateTime
        }
    }
}
