package com.news.search.service

import com.news.search.controller.response.PageResult
import com.news.search.infrastructure.KakaoWebRepositoryImpl
import com.news.search.infrastructure.NaverWebRepositoryImpl
import com.news.search.service.response.PageQueryResult
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
class WebQueryServiceItTest extends Specification {
    @Autowired
    WebQueryService webQueryService

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry

    @SpringBean
    KakaoWebRepositoryImpl kakaoWebRepository = Mock()

    @SpringBean
    NaverWebRepositoryImpl naverWebRepository = Mock()

    def "정상상황에서는 Circuit의 상태가 CLOSED이고 naver쪽으로 호출이 들어간다."() {
        given:
        def keyword = 'HTTP'
        def page = 1
        def size = 10

        when:
        webQueryService.search(keyword, page, size)

        then:
        1 * naverWebRepository.search(keyword, page, size) >> new PageQueryResult<>(1, 10, 0, [])

        and:
        def circuitBreaker = circuitBreakerRegistry.getAllCircuitBreakers().stream().findFirst().get()
        circuitBreaker.state == CircuitBreaker.State.CLOSED

        and:
        0 * kakaoWebRepository.search(*_)
    }

    def "circuit이 open되면 kakao쪽으로 요청을 한다."() {
        given:
        def keyword = 'HTTP'
        def page = 1
        def size = 10
        def kakaoResponse = new PageQueryResult<>(1, 10, 0, [])

        def config = CircuitBreakerConfig.custom()
                .slidingWindowSize(1)
                .minimumNumberOfCalls(1)
                .failureRateThreshold(50)
                .build()
        circuitBreakerRegistry.circuitBreaker("naverSearch", config)

        and: "naver쪽은 항상 예외가 발생한다."
        naverWebRepository.search(keyword, page, size) >> { throw new RuntimeException("error!") }

        when:
        def result = webQueryService.search(keyword, page, size)

        then: "kakao쪽으로 Fallback 된다."
        1 * kakaoWebRepository.search(keyword, page, size) >> kakaoResponse

        and: "circuit이 OPEN된다."
        def circuitBreaker = circuitBreakerRegistry.getAllCircuitBreakers().stream().findFirst().get()
        circuitBreaker.state == CircuitBreaker.State.OPEN

        and:
        result == kakaoResponse
    }
}
