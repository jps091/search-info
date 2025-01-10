package com.news.test;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class TestSearchInfoJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public Long save(SearchInfo searchInfo) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("search_info")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("query", searchInfo.getQuery());
        parameters.put("search_count", searchInfo.getSearchCount());
        parameters.put("event_date_time", searchInfo.getEventDateTime());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        log.info("key={}", key.longValue());

        return key.longValue();
    }

    public void saveAll(List<SearchInfo> searchInfoList) {
        String sql = "INSERT INTO search_info (search_count, query, event_date_time) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        SearchInfo searchInfo = searchInfoList.get(i);
                        ps.setInt(1, searchInfo.getSearchCount());
                        ps.setString(2, searchInfo.getQuery());
                        ps.setTimestamp(3, Timestamp.valueOf(searchInfo.getEventDateTime()));
                    }

                    @Override
                    public int getBatchSize() {
                        return searchInfoList.size();
                    }
                }
        );
    }

    public void incrementSearchCount(List<Integer> ids) {
        String sql = """
        UPDATE search_info
        SET search_count = search_count + 1
        WHERE id IN (%s)
    """;

        // IN 절에 포함될 id 리스트를 쉼표로 연결
        String inClause = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // 완성된 SQL 쿼리
        String formattedSql = String.format(sql, inClause);

        // 업데이트 실행
        jdbcTemplate.update(formattedSql);
    }

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
