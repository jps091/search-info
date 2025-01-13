package com.search.websearch.service.port

import com.news.search.service.response.PageQueryResult
import com.news.search.service.response.SearchQueryResponse
import com.search.searchinfo.service.result.SearchInfoQueryResult
import com.search.websearch.service.result.WebSearchPageResult
import com.search.websearch.service.result.WebSearchResult

interface WebRepository {
    fun search(query: String, page: Int, size: Int): WebSearchPageResult<WebSearchResult>
}