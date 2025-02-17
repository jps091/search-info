package com.search.error

enum class ErrorType(override val description: String): ErrorTypeIfs {
    EXTERNAL_API_ERROR("Error calling external API."),
    UNKNOWN("Server error. Please try again later."),
    INVALID_PARAMETER("Invalid request."),
    NO_RESOURCE("Does not exist. It is not a resource.")
}