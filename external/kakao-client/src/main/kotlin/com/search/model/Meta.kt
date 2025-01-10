package com.search.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Meta(
        @JsonProperty("is_end") val isEnd : Boolean,
        @JsonProperty("pageable_count") val pageableCount : Int,
        @JsonProperty("total_count") val totalCount : Int
)
