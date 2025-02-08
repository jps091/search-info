package com.search.domain.websearch.event

import java.time.LocalDateTime

data class EventRequest(
        var query: String,
        var timestamp: LocalDateTime
)
