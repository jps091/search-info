package com.news.searchinfo.infrastructure;

import com.news.dailystat.service.response.DailyStatQueryResponse;
import com.news.search.service.response.SearchInfoQueryResponse;
import com.news.searchinfo.model.SearchInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class SearchInfoJdbcQueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<DailyStatQueryResponse> findTopQuery(int limit){
        String sql = """
            SELECT query, search_count
            FROM search_info
            ORDER BY search_count DESC
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, statResponseRowMapper(), limit);
    }

    public List<SearchInfoQueryResponse> findByQueryList(List<String> queryList) {
        String sql = """
        SELECT id, query
        FROM search_info
        WHERE query IN (:queryList)
    """;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("queryList", queryList);

        return namedParameterJdbcTemplate.query(sql, parameters, idRowMapper());
    }

    private RowMapper<DailyStatQueryResponse> statResponseRowMapper() {
        return (rs, rowNum) -> new DailyStatQueryResponse(
                rs.getString("query"),        // 'query' 컬럼 값
                rs.getInt("search_count")     // 'COUNT(query)' 결과
        );
    }

    private RowMapper<SearchInfoQueryResponse> idRowMapper() {
        return (rs, rowNum) -> new SearchInfoQueryResponse(
                rs.getInt("id"),
                rs.getString("query")
        );
    }
}
