package com.search.websearch.infrastructure

import com.search.feign.NaverClient
import com.search.model.Item
import com.search.websearch.service.port.WebRepository
import com.search.websearch.service.result.WebSearchPageResult
import com.search.websearch.service.result.WebSearchResult
import org.springframework.stereotype.Repository

@Repository
class NaverWebRepositoryImpl(
        val naverClient: NaverClient
) : WebRepository{
    override fun search(query: String, page: Int, size: Int): WebSearchPageResult<WebSearchResult> {
        val searchResult = naverClient.search(query, page, size)
        val contents = searchResult.items.map { toWebSearchResult(it) }.toList()
        return WebSearchPageResult(page, size, searchResult.total, contents)
    }

    private fun toWebSearchResult(item: Item): WebSearchResult{
        return WebSearchResult(
                title = item.title,
                link = item.link,
                description = item.description
        )
    }
}
