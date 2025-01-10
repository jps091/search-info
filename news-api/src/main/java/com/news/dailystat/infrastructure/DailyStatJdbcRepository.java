package com.news.dailystat.infrastructure;

import com.news.dailystat.model.DailyStat;
import com.news.dailystat.service.response.DailyStatQueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class DailyStatJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public Long save(DailyStat dailyStat) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("daily_stat")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("query", dailyStat.getQuery());
        parameters.put("event_date_time", dailyStat.getEventDateTime());

        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        log.info("key={}", key.longValue());

        return key.longValue();
    }

    public void saveAll(List<DailyStat> dailyStats) {
        String sql = "INSERT INTO daily_stat (query, event_date_time) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(
                sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        DailyStat dailyStat = dailyStats.get(i);
                        ps.setString(1, dailyStat.getQuery());
                        ps.setTimestamp(2, Timestamp.valueOf(dailyStat.getEventDateTime()));
                    }

                    @Override
                    public int getBatchSize() {
                        return dailyStats.size();
                    }
                }
        );
    }

    public long countByQueryAndEventDateTimeBetween(String query, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COUNT(*) FROM daily_stat WHERE query = ? AND event_date_time BETWEEN ? AND ?";
        return jdbcTemplate.queryForObject(sql, Long.class, query, start, end);
    }

    public List<DailyStatQueryResponse> findTopQuery(int limit){
        String sql = """
            SELECT query, COUNT(query) AS query_count
            FROM daily_stat
            GROUP BY query
            ORDER BY query_count DESC
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, statResponseRowMapper(), limit);
    }

    private RowMapper<DailyStatQueryResponse> statResponseRowMapper() {
        return (rs, rowNum) -> new DailyStatQueryResponse(
                rs.getString("query"),        // 'query' 컬럼 값
                rs.getLong("query_count")     // 'COUNT(query)' 결과
        );
    }
}
