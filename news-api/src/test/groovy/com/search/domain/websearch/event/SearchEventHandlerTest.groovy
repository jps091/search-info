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
}
