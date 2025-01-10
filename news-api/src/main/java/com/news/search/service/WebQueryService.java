package com.news.search.service;

import com.news.search.service.port.WebRepository;
import com.news.search.service.response.PageQueryResult;
import com.news.search.service.response.SearchQueryResponse;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebQueryService {

    @Qualifier("naverWebRepositoryImpl")
    private final WebRepository naverWebRepository;

    @Qualifier("kakaoWebRepositoryImpl")
    private final WebRepository kakaoWebRepository;

    @CircuitBreaker(name = "naverSearch", fallbackMethod = "searchFallBack")
    public PageQueryResult<SearchQueryResponse> search(String query, int page, int size) {
        log.info("[WebQueryService] naver query = {}, page = {}, size = {}",query, page, size);
        return naverWebRepository.search(query, page, size);
    }

    public PageQueryResult<SearchQueryResponse> searchFallBack(String query, int page, int size, Throwable throwable) {
        if (throwable instanceof CallNotPermittedException) {
            return handleOpenCircuit(query, page, size);
        }
        return handleException(query, page, size, throwable);
    }

    private PageQueryResult<SearchQueryResponse> handleOpenCircuit(String query, int page, int size) {
        log.warn("[WebQueryService] Circuit Breaker is open! Fallback to kakao search.");
        return kakaoWebRepository.search(query, page, size);
    }

    private PageQueryResult<SearchQueryResponse> handleException(String query, int page, int size, Throwable throwable) {
        log.error("[WebQueryService] An error occurred! Fallback to kakao search. errorMessage={}", throwable.getMessage());
        return kakaoWebRepository.search(query, page, size);
    }
}
