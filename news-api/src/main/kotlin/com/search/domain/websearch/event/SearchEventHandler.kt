package com.search.domain.websearch.event

import com.search.domain.searchinfo.infrastructure.SearchInfoCommandRepository
import com.search.domain.searchinfo.infrastructure.SearchInfoQueryRepository
import com.search.domain.searchinfo.infrastructure.result.SearchInfoQueryResult
import com.search.domain.searchinfo.model.SearchInfo
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SearchEventHandler(
        private val queryRepository: SearchInfoQueryRepository,
        private val commandRepository: SearchInfoCommandRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Async
    @EventListener
    fun handleEvent(request: EventRequest){
        log.info("[SearchEventHandler] handleEvent: {}", request)
        val inputQueryList = parseInputQueryList(request.query)
        val savedQueryList = queryRepository.findByQueryList(inputQueryList)

        if(savedQueryList.isNotEmpty()){
            val searchInfoList = extractNewSearchQueryList(inputQueryList, savedQueryList, request.timestamp)
            commandRepository.saveAll(searchInfoList)

            val ids = getSavedSearchInfoIds(savedQueryList)
            commandRepository.increaseSearchCount(ids)
            return
        }

        val inputNewSearchInfoList = SearchInfo.createList(inputQueryList, request.timestamp)
        commandRepository.saveAll(inputNewSearchInfoList)
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
}