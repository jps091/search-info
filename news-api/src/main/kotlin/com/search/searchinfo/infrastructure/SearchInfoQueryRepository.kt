package com.search.searchinfo.infrastructure

import com.search.searchinfo.service.result.SearchInfoQueryResult
import com.search.websearch.service.result.WebSearchResult
import com.search.searchinfo.service.result.TopQueryResult
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class SearchInfoQueryRepository(
        private val jdbcTemplate: JdbcTemplate,
        private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun findTopQuery(limit: Int) : List<TopQueryResult>{
        val sql = """
        SELECT query, search_count
        FROM search_info
        ORDER BY search_count DESC
        LIMIT ?
        """
        return jdbcTemplate.query(sql, topQueryRowMapper(), limit)
    }

    fun findByQueryList(queryList: List<String>): List<SearchInfoQueryResult>{
        val sql = """
        SELECT id, query
        FROM search_info
        WHERE query IN (:queryList)
        """

        val parameters = MapSqlParameterSource().apply {
            addValue("queryList", queryList)
        }

        return namedParameterJdbcTemplate.query(sql, parameters, idRowMapper())
    }

    private fun topQueryRowMapper(): RowMapper<TopQueryResult>{
        return RowMapper{ rs, _ ->
            TopQueryResult(
                    query = rs.getString("query"),
                    searchCount = rs.getInt("search_count")
            )
        }
    }

    private fun idRowMapper(): RowMapper<SearchInfoQueryResult>{
        return RowMapper{ rs, _ ->
            SearchInfoQueryResult(
                    id = rs.getInt("id"),
                    query = rs.getString("query")
            )
        }
    }
}