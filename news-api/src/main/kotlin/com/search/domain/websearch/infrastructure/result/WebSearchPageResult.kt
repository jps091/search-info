package com.search.domain.websearch.infrastructure.result

data class WebSearchPageResult<T>(val page: Int,
                                  val size: Int,
                                  val totalElements: Int,
                                  val contents: List<T>
)
