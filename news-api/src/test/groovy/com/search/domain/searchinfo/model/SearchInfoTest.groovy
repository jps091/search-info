package com.search.domain.searchinfo.model

import spock.lang.Specification

import java.time.LocalDateTime

class SearchInfoTest extends Specification {

    def "키워드와 생성 시간을 통해 SearchInfo를 만들수 있다."(){
        given:
        def keyword = "HTTP"
        def createdAt = LocalDateTime.of(2024, 9, 10, 23, 58)

        when:
        def result = SearchInfo.create(keyword, createdAt)

        then:
        with(result){
            searchCount == 1
            query == keyword
            eventDateTime == createdAt
            id == null
        }
    }

    def "키워드를 리스트로 전달하면, 결과도 SearchInfo 리스트로 반환 한다"(){
        given:
        def keywords = List.of("HTTP", "JAVA")
        def createdAt = LocalDateTime.of(2024, 9, 10, 23, 58)

        when:
        def results = SearchInfo.createList(keywords, createdAt)

        then:
        with(results){
            size() == 2
            results*.query.containsAll(["JAVA", "HTTP"])
            results*.searchCount.every() {it == 1}
            results*.eventDateTime.every() {it == createdAt}
        }
    }
}

