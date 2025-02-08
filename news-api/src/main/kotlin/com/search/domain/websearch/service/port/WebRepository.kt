package com.search.domain.websearch.service.port

import com.search.domain.websearch.infrastructure.result.WebSearchPageResult
import com.search.domain.websearch.infrastructure.result.WebSearchResult

interface WebRepository {
    fun search(query: String, page: Int, size: Int): WebSearchPageResult<WebSearchResult>
}