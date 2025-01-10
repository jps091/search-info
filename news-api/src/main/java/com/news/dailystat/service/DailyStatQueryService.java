package com.news.dailystat.service;

import com.news.dailystat.infrastructure.DailyStatJdbcRepository;
import com.news.dailystat.infrastructure.DailyStatJpaRepository;
import com.news.dailystat.service.response.DailyStatQueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DailyStatQueryService {

    private final DailyStatJdbcRepository dailyStatJdbcRepository;
    private final DailyStatJpaRepository dailyStatJpaRepository;

    private static final int PAGE = 0;
    private static final int SIZE = 15;

    public DailyStatQueryResponse findQueryCount(String query, LocalDate localDate){
        long count = dailyStatJdbcRepository.countByQueryAndEventDateTimeBetween(
                query,
                localDate.withDayOfMonth(1).atStartOfDay(),
                localDate.withDayOfMonth(localDate.lengthOfMonth()).atTime(LocalTime.MAX)
        );
        return new DailyStatQueryResponse(query, count);
    }

    public DailyStatQueryResponse findQueryCountByJpa(String query, LocalDate localDate){
        long count = dailyStatJpaRepository.countByQueryAndEventDateTimeBetween(
                query,
                localDate.withDayOfMonth(1).atStartOfDay(),
                localDate.withDayOfMonth(localDate.lengthOfMonth()).atTime(LocalTime.MAX)
        );
        return new DailyStatQueryResponse(query, count);
    }

    public List<DailyStatQueryResponse> findTop15Query() {
        return dailyStatJdbcRepository.findTopQuery(SIZE);
    }

    public List<DailyStatQueryResponse> findTop5QueryByJpa() {
        Pageable pageable = PageRequest.of(PAGE, SIZE);
        return dailyStatJpaRepository.findTopQuery(pageable);
    }
}
