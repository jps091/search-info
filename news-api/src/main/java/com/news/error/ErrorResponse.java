package com.news.error;

import com.news.exception.ErrorType;

public record ErrorResponse(String errorMessage, ErrorType errorType) {
}
