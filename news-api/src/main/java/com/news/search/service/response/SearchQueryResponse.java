package com.news.search.service.response;

import lombok.Builder;

@Builder
public record SearchQueryResponse(
        String title,
        String link,
        String description
) {
}
