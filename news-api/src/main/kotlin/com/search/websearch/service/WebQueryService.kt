package com.search.websearch.service

import com.search.websearch.service.port.WebRepository
import com.search.websearch.service.result.WebSearchPageResult
import com.search.websearch.service.result.WebSearchResult
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class WebQueryService (
        @Qualifier("naverWebRepositoryImpl") private val naverWebRepository: WebRepository,
        @Qualifier("kakaoWebRepositoryImpl") private val kakaoWebRepository: WebRepository
){
    private val log = LoggerFactory.getLogger(this::class.java)

    @CircuitBreaker(name = "naverSearch", fallbackMethod = "searchFallBack")
    fun search(query: String, page: Int, size: Int) : WebSearchPageResult<WebSearchResult>{
        log.info("[WebQueryService] naver query = {}, page = {}, size = {}", query, page, size)
        return naverWebRepository.search(query, page, size)
    }

    fun searchFallBack(query: String, page: Int, size: Int, throwable: Throwable) : WebSearchPageResult<WebSearchResult>{
        if (throwable is CallNotPermittedException){
            return handleOpenCircuit(query, page, size)
        }
        return handleException(query, page, size, throwable)
    }

    private fun handleOpenCircuit(query: String, page: Int, size: Int): WebSearchPageResult<WebSearchResult>{
        log.warn("[WebQueryService] Circuit Breaker is open! Fallback to kakao search.")
        return kakaoWebRepository.search(query, page, size)
    }

    private fun handleException(query: String, page: Int, size: Int, throwable: Throwable): WebSearchPageResult<WebSearchResult> {
        log.error("[WebQueryService] An error occurred! Fallback to kakao search. errorMessage={}", throwable.message)
        return kakaoWebRepository.search(query, page, size)
    }
}