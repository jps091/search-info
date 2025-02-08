package com.search.domain.websearch.controller.response

data class PageSearchResponse<T>(val page: Int,
                                 val size: Int,
                                 val totalElements: Int,
                                 val contents: List<T>
)
