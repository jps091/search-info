package com.news.model;

import lombok.Builder;

import java.util.List;

@Builder
public record NaverWebResponse(String lastBuildDate, int total, int start, int display, List<Item> items) {
}
