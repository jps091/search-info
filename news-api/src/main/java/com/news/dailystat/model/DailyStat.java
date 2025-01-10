package com.news.dailystat.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class DailyStat {

    private final Long id;

    private final String query;

    private final LocalDateTime eventDateTime;

    public static DailyStat create(String query, LocalDateTime eventDateTime){
        return DailyStat.builder()
                .query(query)
                .eventDateTime(eventDateTime)
                .build();
    }

    public static List<DailyStat> create(List<String> queryList, LocalDateTime eventDateTime){
        return queryList.stream()
                .map(query -> DailyStat.create(query, eventDateTime))
                .toList();
    }
}
