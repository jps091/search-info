package com.news.error;

public record NaverErrorResponse(
        String errorMessage,
        String errorCode
) {}
