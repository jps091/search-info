package com.news.error;

import com.search.error.ErrorType;

public record ErrorResponse(String errorMessage, ErrorType errorType) {
}
