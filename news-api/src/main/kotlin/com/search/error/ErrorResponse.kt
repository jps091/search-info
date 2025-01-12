package com.search.error

data class ErrorResponse(
        val errorMessage: String,
        val errorType: ErrorType
)