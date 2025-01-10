package com.search.exception

import com.search.error.ErrorType

data class ApiException(
        val errorMessage: String,
        val errorType: ErrorType,
        val httpStatusCode: Int
) : RuntimeException()