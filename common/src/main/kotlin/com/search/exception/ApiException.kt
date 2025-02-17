package com.search.exception
import com.search.error.ErrorTypeIfs

data class ApiException(
        val errorMessage: String,
        val errorType: ErrorTypeIfs,
        val httpStatusCode: Int
) : RuntimeException()