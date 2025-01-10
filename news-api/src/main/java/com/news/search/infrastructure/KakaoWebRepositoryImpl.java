package com.news.search.infrastructure;

import com.news.feign.KakaoClient;
import com.news.model.Document;
import com.news.model.KakaoWebResponse;
import com.news.search.service.port.WebRepository;
import com.news.search.service.response.PageQueryResult;
import com.news.search.service.response.SearchQueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class KakaoWebRepositoryImpl implements WebRepository {
    private final KakaoClient kakaoClient;

    @Override
    public PageQueryResult<SearchQueryResponse> search(String query, int page, int size) {
        KakaoWebResponse response = kakaoClient.search(query, page, size);
        List<SearchQueryResponse> responses = response.documents().stream()
                .map(this::convertToSearchResponse)
                .toList();
        return new PageQueryResult<>(page, size, response.meta().totalCount(), responses);
    }

    private SearchQueryResponse convertToSearchResponse(Document document){
        return SearchQueryResponse.builder()
                .title(document.title())
                .link(document.url())
                .description(document.contents())
                .build();
    }
}
