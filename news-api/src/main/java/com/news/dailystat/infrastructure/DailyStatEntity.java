package com.news.dailystat.infrastructure;

import com.news.dailystat.model.DailyStat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "daily_stat")
public class DailyStatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "query",length = 200)
    private String query;

    @Column(name = "event_date_time")
    private LocalDateTime eventDateTime;

    public static DailyStatEntity from(DailyStat dailyStat){
        DailyStatEntity dailyStatEntity = new DailyStatEntity();
        dailyStatEntity.id = dailyStat.getId();
        dailyStatEntity.query = dailyStat.getQuery();
        dailyStatEntity.eventDateTime = dailyStat.getEventDateTime();
        return dailyStatEntity;
    }
}
