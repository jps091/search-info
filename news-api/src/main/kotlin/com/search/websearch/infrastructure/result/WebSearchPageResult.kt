package com.search.websearch.infrastructure.result

data class WebSearchPageResult<T>(val page: Int,
                                  val size: Int,
                                  val totalElements: Int,
                                  val contents: List<T>
)
