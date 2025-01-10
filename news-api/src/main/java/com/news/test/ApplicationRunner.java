//package com.news;
//
//import com.news.dailystat.model.DailyStat;
//import com.news.dailystat.service.port.DailyStatRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@RequiredArgsConstructor
//@Component
//public class ApplicationRunner implements CommandLineRunner {
//
//    private final DailyStatRepository dailyStatRepository;
//    @Override
//    public void run(String... args) throws Exception {
//        DailyStat stat1 = DailyStat.create("tesla", LocalDateTime.now().plusMonths(1));
//        DailyStat stat2 = DailyStat.create("tesla", LocalDateTime.now().plusMonths(1));
//        DailyStat stat3 = DailyStat.create("tesla", LocalDateTime.now().plusMonths(1));
//        DailyStat stat4 = DailyStat.create("HTML", LocalDateTime.now().plusMonths(1));
//        DailyStat stat5 = DailyStat.create("HTML", LocalDateTime.now().plusMonths(1));
//        DailyStat stat6 = DailyStat.create("HTML", LocalDateTime.now().plusMonths(1));
//        DailyStat stat7 = DailyStat.create("HTML", LocalDateTime.now().plusMonths(1));
//
//        DailyStat stat8 = DailyStat.create("HTML", LocalDateTime.now().plusMonths(1));
//        DailyStat stat9 = DailyStat.create("CSS", LocalDateTime.now().plusMonths(1));
//
//        DailyStat stat10 = DailyStat.create("React", LocalDateTime.now().plusMonths(1));
//
//        DailyStat stat11 = DailyStat.create("React", LocalDateTime.now().plusMonths(1));
//
//        DailyStat stat12 = DailyStat.create("mysql", LocalDateTime.now().plusMonths(1));
//
//        dailyStatRepository.saveAll(List.of(stat1, stat2, stat3, stat4, stat5, stat6, stat7, stat8, stat9,
//                stat10, stat11, stat12, stat3));
//    }
//}
