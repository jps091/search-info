package com.search.websearch.service.response

data class PageQueryResult<T>(val page: Int,
                              val size: Int,
                              val totalElements: Int,
                              val contents: List<T>
)
