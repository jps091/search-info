package com.search.websearch.event

import java.time.LocalDateTime

data class EventRequest(
        var query: String,
        var timestamp: LocalDateTime
)
