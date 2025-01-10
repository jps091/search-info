package com.news.test;

import com.news.dailystat.model.DailyStat;
import com.news.dailystat.service.DailyStatCommandService;
import com.news.search.service.event.SearchEvent;
import com.news.search.service.response.SearchInfoQueryResponse;
import com.news.searchinfo.model.SearchInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestSearchEventHandler {

    private final DailyStatCommandService dailyStatCommandService;

    //@Async
    //@EventListener
    public void handleEvent(SearchEvent event){
        log.info("[SearchEventHandler] handleEvent: {}", event);
        List<String> eventQueryList = parseString(event.query());
        List<DailyStat> dailyStats = DailyStat.create(eventQueryList, event.timestamp());
        dailyStatCommandService.saveAll(dailyStats);
    }

    // 문자열 파싱 메서드
    private List<String> parseString(String input) {
        // 입력 문자열을 공백 기준으로 분리
        String[] words = input.split(" ");
        List<String> result = new ArrayList<>();

        for (String word : words) {
            // 숫자로만 이루어진 단어를 필터링
            if (isNumeric(word)) {
                continue;
            }

            // 영어와 한글 문자만 필터링
            String filteredWord = word.replaceAll("[^a-zA-Z가-힣0-9]", "");

            // 필터링된 결과가 비어 있지 않으면 추가
            if (!filteredWord.isEmpty()) {
                // 영어는 소문자로 변환
                result.add(filteredWord.toLowerCase());
            }
        }

        return result;
    }

    // 문자열이 숫자로만 이루어져 있는지 확인하는 유틸리티 메서드
    private boolean isNumeric(String str) {
        return str.matches("\\d+"); // 문자열이 숫자(0-9)로만 이루어져 있으면 true
    }
}
