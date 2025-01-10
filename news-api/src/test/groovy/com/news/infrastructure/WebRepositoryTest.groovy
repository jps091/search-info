package com.news.infrastructure

import com.news.feign.NaverClient
import com.news.model.Item
import com.news.model.NaverWebResponse
import com.news.search.infrastructure.NaverWebRepositoryImpl
import com.news.search.service.port.WebRepository
import spock.lang.Specification

class WebRepositoryTest extends Specification {

    WebRepository webRepository
    NaverClient naverClient = Mock()

    void setup(){
        webRepository = new NaverWebRepositoryImpl(naverClient)
    }

    def "search 호출시 적절한 데이터 형식으로 변환한다."(){
        given:
        def items = [
                new Item("뉴스1", "www.test1.com", "test1"),
                new Item("뉴스2", "www.test2.com", "test2")
        ]
        def response = NaverWebResponse.builder()
                .lastBuildDate("Tue, 31 Dec 2024 15:31:00 +0900")
                .total(10)
                .start(1)
                .display(10)
                .items(items)
                .build()

        and:
        1 * naverClient.search("test", 1, 10) >> response

        when:
        def result = webRepository.search("test", 1, 10)

        then:
        verifyAll {
            result.size() == 10
            result.page() == 1
            result.totalElements() == 10
            result.contents().size() == 2
        }
    }
}
