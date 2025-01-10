package com.news.search.service.port;

import com.news.search.service.response.PageQueryResult;
import com.news.search.service.response.SearchQueryResponse;

public interface WebRepository {
    PageQueryResult<SearchQueryResponse> search(String query, int page, int size);
}
