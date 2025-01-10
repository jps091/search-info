package com.news.search.controller.response;

import lombok.Builder;

@Builder
public record SearchResponse(
        String title,
        String link,
        String description
) {
}
