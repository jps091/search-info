package com.search.error

enum class ErrorType(val description: String) {
    EXTERNAL_API_ERROR("Error calling external API."),
    UNKNOWN("Server error. Please try again later."),
    INVALID_PARAMETER("Invalid request."),
    NO_RESOURCE("Does not exist. It is not a resource.")
}