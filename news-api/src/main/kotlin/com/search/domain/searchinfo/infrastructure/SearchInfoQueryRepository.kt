package com.search.domain.searchinfo.infrastructure

import com.search.domain.searchinfo.infrastructure.result.SearchInfoQueryResult
import com.search.domain.searchinfo.infrastructure.result.TopQueryResult
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
private const val LIMIT = 15
@Repository
class SearchInfoQueryRepository(
        private val jdbcTemplate: JdbcTemplate,
        private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun findTopQuery() : List<TopQueryResult>{
        val sql = """
        SELECT query, search_count
        FROM search_info
        ORDER BY search_count DESC
        LIMIT ?
        """
        return jdbcTemplate.query(sql, topQueryRowMapper(), LIMIT)
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
                    count = rs.getInt("search_count")
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