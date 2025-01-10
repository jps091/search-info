package com.news.infrastructure

import com.news.dailystat.infrastructure.DailyStatEntity
import com.news.dailystat.infrastructure.DailyStatJdbcRepository
import com.news.dailystat.infrastructure.DailyStatJpaRepository
import com.news.dailystat.model.DailyStat
import com.news.feign.NaverClient
import jakarta.persistence.EntityManager
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DailyStatJpaRepositoryTest extends Specification {
    @Autowired
    EntityManager entityManager

    @Autowired
    DailyStatJdbcRepository dailyStatJdbcRepository;

    @Autowired
    DailyStatJpaRepository dailyStatJpaRepository;

    @SpringBean
    NaverClient naverClient = Mock()


    def "저장이 된다."() {
        given:
        def givenQuery = "HTTP"

        when:
        def dailyStat = DailyStat.create(givenQuery, LocalDateTime.now())
        def savedId = dailyStatJdbcRepository.save(dailyStat)


        then:
        verifyAll {
            savedId != null
        }
    }

    def "쿼리의 카운트를 조회한다."() {
        given:
        def givenQuery = 'HTTP'
        def now = LocalDateTime.of(2024, 5, 1, 0,0,0)
        def nowLocalDate = LocalDate.of(2024, 5, 1)
        def stat1 = DailyStat.create(givenQuery, now.plusMinutes(10))
        def stat2 = DailyStat.create(givenQuery, now.minusMinutes(1))
        def stat3 = DailyStat.create(givenQuery, now.plusMinutes(10))
        def stat4 = DailyStat.create('JAVA', now.plusMinutes(10))

        dailyStatJdbcRepository.save(stat1)
        dailyStatJdbcRepository.save(stat2)
        dailyStatJdbcRepository.save(stat3)
        dailyStatJdbcRepository.save(stat4)

        when:
        def result = dailyStatJdbcRepository.countByQueryAndEventDateTimeBetween(givenQuery, now, now.plusMonths(1))

        then:
        result == 2
    }

    def "벌크 쿼리의 카운트를 조회한다."() {
        given:
        def givenQuery = 'HTTP'
        def now = LocalDateTime.of(2024, 5, 1, 0,0,0)
        def stat1 = DailyStat.create(givenQuery, now.plusMinutes(10))
        def stat2 = DailyStat.create(givenQuery, now.minusMinutes(1))
        def stat3 = DailyStat.create(givenQuery, now.plusMinutes(10))
        def stat4 = DailyStat.create('JAVA', now.plusMinutes(10))


        dailyStatJdbcRepository.saveAll([stat1, stat2, stat3, stat4])

        when:
        def result = dailyStatJdbcRepository.countByQueryAndEventDateTimeBetween(givenQuery, now, now.plusMonths(1))

        then:
        result == 2
    }

    def "JPA 쿼리의 카운트를 조회한다."() {
        given:
        def givenQuery = 'HTTP'
        def now = LocalDateTime.of(2024, 5, 1, 0,0,0)
        def stat1 = DailyStat.create(givenQuery, now.plusMinutes(10))
        def stat2 = DailyStat.create(givenQuery, now.minusMinutes(1))
        def stat3 = DailyStat.create(givenQuery, now.plusMinutes(10))
        def stat4 = DailyStat.create('JAVA', now.plusMinutes(10))


        dailyStatJpaRepository.saveAll([DailyStatEntity.from(stat1), DailyStatEntity.from(stat2), DailyStatEntity.from(stat3), DailyStatEntity.from(stat4)])

        when:
        def result = dailyStatJpaRepository.countByQueryAndEventDateTimeBetween(givenQuery, now, now.plusDays(1))

        then:
        result == 2
    }

    def "가장 많이 검색된 쿼리 키워드를 개수와 함께 상위 3개반환한다."() {
        given:
        def now = LocalDateTime.now()
        def stat1 = DailyStat.create('HTML', now.plusMinutes(10))
        def stat2 = DailyStat.create('HTML', now.plusMinutes(10))
        def stat3 = DailyStat.create('HTML', now.plusMinutes(10))
        def stat4 = DailyStat.create('SPOCK', now.plusMinutes(10))
        def stat5 = DailyStat.create('SPOCK', now.plusMinutes(10))
        def stat6 = DailyStat.create('SPOCK', now.plusMinutes(10))
        def stat7 = DailyStat.create('SPOCK', now.plusMinutes(10))
        def stat8 = DailyStat.create('SPRING', now.plusMinutes(10))
        def stat9 = DailyStat.create('SPRING', now.plusMinutes(10))
        def stat10 = DailyStat.create('OS', now.plusMinutes(10))

        dailyStatJdbcRepository.saveAll([stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8, stat9, stat10])

        when:
        def response = dailyStatJdbcRepository.findTopQuery(3)

        then:
        verifyAll {
            response.size() == 3
            response[0].query() == 'SPOCK'
            response[0].count() == 4
            response[1].query() == 'HTML'
            response[1].count() == 3
            response[2].query() == 'SPRING'
            response[2].count() == 2
        }
    }
}
