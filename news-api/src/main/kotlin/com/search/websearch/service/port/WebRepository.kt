package com.search.websearch.service.port

import com.news.search.service.response.PageQueryResult
import com.news.search.service.response.SearchQueryResponse
import com.search.searchinfo.infrastructure.result.SearchInfoQueryResult
import com.search.websearch.infrastructure.result.WebSearchPageResult
import com.search.websearch.infrastructure.result.WebSearchResult

interface WebRepository {
    fun search(query: String, page: Int, size: Int): WebSearchPageResult<WebSearchResult>
}