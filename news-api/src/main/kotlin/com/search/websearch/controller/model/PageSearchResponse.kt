package com.search.websearch.controller.model

data class PageSearchResponse<T>(val page: Int,
                                 val size: Int,
                                 val totalElements: Int,
                                 val contents: List<T>
)
