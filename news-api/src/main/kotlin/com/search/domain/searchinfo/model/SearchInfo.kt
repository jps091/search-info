package com.search.domain.searchinfo.model

import java.time.LocalDateTime

class SearchInfo private constructor(
        val id: Int? = null,
        val searchCount: Int,
        val query: String,
        val eventDateTime: LocalDateTime
) { companion object{
        @JvmStatic
        fun create(query: String, eventDateTime: LocalDateTime): SearchInfo {
            require(query.isNotBlank()) { "Query cannot be blank" }
            return SearchInfo(
                    id = null,
                    searchCount = 1,
                    query = query,
                    eventDateTime = eventDateTime
                )
            }
        @JvmStatic
        fun createList(queryList: List<String>, eventDateTime: LocalDateTime): List<SearchInfo> {
            return queryList.map { query -> create(query, eventDateTime) }
        }
    }
}

