package com.search.searchinfo.infrastructure

import com.search.searchinfo.model.SearchInfo
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.sql.PreparedStatement
import java.sql.Timestamp

@Repository
class SearchInfoCommandRepository(
        private val jdbcTemplate: JdbcTemplate,
        private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun save(searchInfo: SearchInfo): Int{
        val jdbcInsert = SimpleJdbcInsert(jdbcTemplate).apply {
            withTableName("search_info")
            usingGeneratedKeyColumns("id")
        }

        val parameters: MutableMap<String, Any> = HashMap()
        parameters["query"] = searchInfo.query
        parameters["search_count"] = searchInfo.searchCount
        parameters["event_date_time"] = searchInfo.eventDateTime

        val key = jdbcInsert.executeAndReturnKey(MapSqlParameterSource(parameters))
        log.info("key={}", key.toInt())

        return key.toInt()
    }

    fun saveAll(searchInfoList: List<SearchInfo>){
        val sql = "INSERT INTO search_info (search_count, query, event_date_time) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                object: BatchPreparedStatementSetter{
                    override fun setValues(ps: PreparedStatement, i: Int) {
                        val searchInfo = searchInfoList[i]
                        ps.setInt(1, searchInfo.searchCount)
                        ps.setString(2, searchInfo.query)
                        ps.setTimestamp(3, Timestamp.valueOf(searchInfo.eventDateTime))
                    }

                    override fun getBatchSize(): Int = searchInfoList.size
                }
        )
    }

    fun increaseSearchCount(ids: List<Int>){
        val sql = """
        UPDATE search_info
        SET search_count = search_count + 1
        WHERE id IN (:ids)
        """

        val parameters = mapOf("ids" to ids)
        namedParameterJdbcTemplate.update(sql, parameters)
    }
}