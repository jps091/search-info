package com.news.dailystat.service;

import com.news.dailystat.infrastructure.DailyStatEntity;
import com.news.dailystat.infrastructure.DailyStatJdbcRepository;
import com.news.dailystat.infrastructure.DailyStatJpaRepository;
import com.news.dailystat.model.DailyStat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DailyStatCommandService {

    private final DailyStatJdbcRepository dailyStatJdbcRepository;
    private final DailyStatJpaRepository dailyStatJpaRepository;

    public void save(DailyStat dailyStat) {
        log.info("save daily stats: {}", dailyStat);
        dailyStatJdbcRepository.save(dailyStat);
    }

    public void saveAll(List<DailyStat> dailyStat) {
        log.info("save daily stats: {}", dailyStat);
        dailyStatJdbcRepository.saveAll(dailyStat);
    }

    public void saveAllByJpa(List<DailyStat> dailyStat) {
        log.info("save daily stats: {}", dailyStat);
        List<DailyStatEntity> dailyStatEntityList = dailyStat.stream()
                .map(DailyStatEntity::from)
                .toList();
        dailyStatJpaRepository.saveAll(dailyStatEntityList);
    }
}
