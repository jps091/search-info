package com.search.websearch.service.port

import com.search.websearch.infrastructure.result.WebSearchPageResult
import com.search.websearch.infrastructure.result.WebSearchResult

interface WebRepository {
    fun search(query: String, page: Int, size: Int): WebSearchPageResult<WebSearchResult>
}