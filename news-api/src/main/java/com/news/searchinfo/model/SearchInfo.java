package com.news.searchinfo.model;


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
public class SearchInfo {

    private final int id;

    private final int searchCount;

    private final String query;

    private final LocalDateTime eventDateTime;

    public static SearchInfo create(String query, LocalDateTime eventDateTime){
        return SearchInfo.builder()
                .query(query)
                .searchCount(1)
                .eventDateTime(eventDateTime)
                .build();
    }

    public static List<SearchInfo> create(List<String> queryList, LocalDateTime eventDateTime){
        return queryList.stream()
                .map(query -> SearchInfo.create(query, eventDateTime))
                .toList();
    }
}
