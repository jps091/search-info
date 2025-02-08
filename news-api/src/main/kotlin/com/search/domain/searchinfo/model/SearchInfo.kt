package com.search.domain.searchinfo.model

import java.time.LocalDateTime

class SearchInfo private constructor(
        val id: Int? = null,
        val searchCount: Int,
        val query: String,
        val eventDateTime: LocalDateTime
) { companion object{
        fun create(query: String, eventDateTime: LocalDateTime): SearchInfo {
            require(query.isNotBlank()) { "Query cannot be blank" }
            return SearchInfo(
                    id = null,
                    searchCount = 1,
                    query = query,
                    eventDateTime = eventDateTime
                )
            }

        fun createList(queryList: List<String>, eventDateTime: LocalDateTime): List<SearchInfo> {
            return queryList.map { query -> create(query, eventDateTime) }
        }
    }
}
