package com.news.search.service.event

import com.news.dailystat.model.DailyStat
import com.news.dailystat.service.DailyStatCommandService
import com.news.test.TestSearchEventHandler
import spock.lang.Specification

import java.time.LocalDateTime

class SearchEventHandlerTest extends Specification {
//    def "handleEvent"() {
//        given:
//        def commandService = Mock(DailyStatCommandService)
//        def eventHandler = new SearchEventHandler(commandService)
//        def event = new SearchEvent("HTTP", LocalDateTime.now())
//
//        when:
//        eventHandler.handleEvent(event)
//
//        then:
//        1 * commandService.save(_ as DailyStat)
//    }

    def "handleEvent2"() {
        given:
        def commandService = Mock(DailyStatCommandService)
        def eventHandler = new TestSearchEventHandler(commandService)
        def event = new SearchEvent("HTTP Java 123 CSS", LocalDateTime.now())

        when:
        eventHandler.handleEvent(event)

        then:
        1 * commandService.saveAll({ List<DailyStat> dailyStats ->
            // DailyStat 리스트의 크기를 검증
            dailyStats.size() == 3 // 숫자 "123" 제외됨, 결과: ["http", "java", "css"]

            // 각 DailyStat의 query 값 검증
            dailyStats*.query.containsAll(["http", "java", "css"])
        })
    }

    def "handleEvent3"() {
        given:
        def commandService = Mock(DailyStatCommandService)
        def eventHandler = new TestSearchEventHandler(commandService)
        def event = new SearchEvent("123  CSS1", LocalDateTime.now())

        when:
        eventHandler.handleEvent(event)

        then:
        1 * commandService.saveAll({ List<DailyStat> dailyStats ->
            // DailyStat 리스트의 크기를 검증
            dailyStats.size() == 1

            // 각 DailyStat의 query 값 검증
            dailyStats*.query.containsAll(["css1"])
        })
    }
}
