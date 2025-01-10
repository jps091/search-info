package com.news.dailystat.infrastructure;

import com.news.dailystat.service.response.DailyStatQueryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyStatJpaRepository extends JpaRepository<DailyStatEntity, Long> {
    long countByQueryAndEventDateTimeBetween(String query, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.news.dailystat.service.response.DailyStatQueryResponse(ds.query, count(ds.query))" +
            "FROM DailyStatEntity ds " +
            "GROUP BY ds.query ORDER BY count(ds.query) DESC")
    List<DailyStatQueryResponse> findTopQuery(Pageable pageable);
}
