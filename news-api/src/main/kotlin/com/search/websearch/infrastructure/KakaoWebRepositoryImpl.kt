package com.search.websearch.infrastructure

import com.search.feign.KakaoClient
import com.search.model.Document
import com.search.websearch.service.port.WebRepository
import com.search.websearch.infrastructure.result.WebSearchPageResult
import com.search.websearch.infrastructure.result.WebSearchResult
import org.springframework.stereotype.Repository

@Repository
class KakaoWebRepositoryImpl(
        val kakaoClient: KakaoClient
) : WebRepository{
    override fun search(query: String, page: Int, size: Int): WebSearchPageResult<WebSearchResult> {
        val searchResult = kakaoClient.search(query,page, size)
        val contents = searchResult.documents.map { toWebSearchResult(it) }
        return WebSearchPageResult(page, size, searchResult.meta.totalCount, contents)
    }

    private fun toWebSearchResult(document: Document): WebSearchResult {
        return WebSearchResult(
                title = document.title,
                link = document.url,
                description = document.contents
        )
    }
}