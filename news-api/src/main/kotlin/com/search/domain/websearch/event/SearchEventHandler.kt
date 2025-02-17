package com.search.domain.websearch.event

import com.search.domain.chatroom.service.ChatRoomService
import com.search.domain.searchinfo.infrastructure.SearchInfoCommandRepository
import com.search.domain.searchinfo.infrastructure.SearchInfoQueryRepository
import com.search.domain.searchinfo.infrastructure.result.SearchInfoQueryResult
import com.search.domain.searchinfo.infrastructure.result.TopQueryResult
import com.search.domain.searchinfo.model.SearchInfo
import com.search.domain.sse.connection.SseConnectionPool
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SearchEventHandler(
        private val sseConnectionPool: SseConnectionPool,
        private val queryRepository: SearchInfoQueryRepository,
        private val commandRepository: SearchInfoCommandRepository,
        private val chatRoomService: ChatRoomService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Async
    @EventListener
    fun handleDatabaseEvent(request: EventRequest){
        log.info("[SearchEventHandler] handleEvent: {}", request)
        val inputQueryList = parseInputQueryList(request.query)
        val savedQueryList = queryRepository.findByQueryList(inputQueryList)
        val oldTopList = queryRepository.findTopQuery()

        if(savedQueryList.isEmpty()){
            val inputNewSearchInfoList = SearchInfo.createList(inputQueryList, request.timestamp)
            commandRepository.saveAll(inputNewSearchInfoList)
            return
        }

        val searchInfoList = extractNewSearchQueryList(inputQueryList, savedQueryList, request.timestamp)
        val ids = getSavedSearchInfoIds(savedQueryList)
        commandRepository.saveAll(searchInfoList)
        commandRepository.increaseSearchCount(ids)

        val newTopList = queryRepository.findTopQuery()

        if(isTopListChanged(oldTopList, newTopList)) {
            log.info("TopList 변경 감지, 전체 순위 업데이트 이벤트 전송")
            sseConnectionPool.sendToAll("rankingUpdate", newTopList)
            processNewChatRooms(oldTopList, newTopList)
            return
        }
    }

    private fun parseInputQueryList(input: String): List<String>{
        return input.split(" ")
                .filterNot { isNumeric(it) }
                .map{it.replace(Regex("[^a-zA-Z가-힣0-9]"), "").lowercase()}
                .filter{it.isNotEmpty()}
    }

    private fun extractNewSearchQueryList(
            inputQueryList: List<String>,
            savedQueryList: List<SearchInfoQueryResult>,
            timeStamp: LocalDateTime): List<SearchInfo>
    {
        val savedResults = getSavedSearchInfoQueries(savedQueryList)
        val newQueryList = getDifference(inputQueryList, savedResults)
        return SearchInfo.createList(newQueryList, timeStamp)
    }

    private fun getSavedSearchInfoIds(queryList: List<SearchInfoQueryResult>): List<Int>{
        return queryList.map{it.id}
    }

    private fun isNumeric(str: String): Boolean{
        return str.matches(Regex("\\d+"))
    }

    private fun getSavedSearchInfoQueries(queryList: List<SearchInfoQueryResult>): List<String>{
        return queryList.map{it.query}
    }

    private fun getDifference(source: List<String>, target: List<String>): List<String> {
        val targetWords = target.toSet()
        return source.filter{it !in targetWords}
    }

    private fun isTopListChanged(oldTopList: List<TopQueryResult>, newTopList: List<TopQueryResult>): Boolean{
        // 리스트 요소를 비교하여 변경 여부 확인
        for (i in oldTopList.indices) {
            if (oldTopList[i] != newTopList[i]) { // 각 요소 비교
                return true
            }
        }
        return false
    }

    private fun processNewChatRooms(oldTopList: List<TopQueryResult>, newTopList: List<TopQueryResult>) {
        val oldKeywords = oldTopList.map { it.query }.toSet()
        val newKeywords = newTopList.map { it.query }.filter { it !in oldKeywords }
        newKeywords.forEach { keyword ->
            chatRoomService.create(keyword)
        }
    }
}