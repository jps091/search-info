package com.news.error;

public record KakaoErrorResponse(
        String errorType,
        String message
) {
}
