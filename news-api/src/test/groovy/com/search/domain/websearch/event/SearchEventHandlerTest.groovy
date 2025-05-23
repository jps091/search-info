package com.search.domain.websearch.event

import com.search.domain.chatroom.service.ChatRoomService
import com.search.domain.searchinfo.infrastructure.SearchInfoCommandRepository
import com.search.domain.searchinfo.infrastructure.SearchInfoQueryRepository
import com.search.domain.searchinfo.infrastructure.result.SearchInfoQueryResult
import com.search.domain.searchinfo.infrastructure.result.TopQueryResult
import com.search.domain.searchinfo.model.SearchInfo
import com.search.domain.sse.connection.SseConnectionPool
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.core.task.SyncTaskExecutor
import org.springframework.core.task.TaskExecutor
import spock.lang.Specification

import java.time.LocalDateTime

class SearchEventHandlerTest extends Specification {

    SseConnectionPool sseConnectionPool = Mock(SseConnectionPool)
    SearchInfoQueryRepository queryRepository = Mock(SearchInfoQueryRepository)
    SearchInfoCommandRepository commandRepository = Mock(SearchInfoCommandRepository)
    ChatRoomService chatRoomService = Mock(ChatRoomService)

    SearchEventHandler eventHandler

    @TestConfiguration
    static class AsyncTestConfig {
        @Bean
        TaskExecutor taskExecutor() {
            return new SyncTaskExecutor()
        }
    }

    void setup(){
        eventHandler = new SearchEventHandler(sseConnectionPool, queryRepository, commandRepository, chatRoomService)
    }

    def "savedQueryList가 빈 값이면 저장만 한다."(){
        given:
        def now = LocalDateTime.now()
        def eventRequest = new EventRequest("HTTP JAVA", now)

        queryRepository.findByQueryList(_ as List<String>) >> []
        SearchInfo.createList(*_) >> List.of(SearchInfo.create("HTTP", now), SearchInfo.create("JAVA", now))

        when:
        eventHandler.handleDatabaseEvent(eventRequest)

        then:
        1 * commandRepository.saveAll(_)
        1 * queryRepository.findTopQuery()
        0 * commandRepository.increaseSearchCount(_)
        0 * sseConnectionPool.sendToAll(_, _)
    }

//    def "savedQueryList가 존재한다면, 저장과 Count를 증가시킨다."(){
//        given:
//        def now = LocalDateTime.now()
//        def eventRequest = new EventRequest("HTTP JAVA", now)
//        def topList = List.of(new TopQueryResult("HTTP", 10), new TopQueryResult("JAVA", 1))
//
//        queryRepository.findByQueryList(_ as List<String>) >> List.of(new SearchInfoQueryResult(1, "HTTP"))
//        queryRepository.findTopQuery() >> topList TODO Mocking을 헀지만 NPE 발생 (원인파악필요)
//
//        when:
//        eventHandler.handleDatabaseEvent(eventRequest)
//
//        then:
//        1 * commandRepository.saveAll(_)
//        2 * queryRepository.findTopQuery()
//        1 * commandRepository.increaseSearchCount(_)
//        0 * sseConnectionPool.sendToAll(_, _)
//    }
}
