package com.news.search.service.event;

import java.time.LocalDateTime;

public record SearchEvent(String query, LocalDateTime timestamp) {
}
