package com.search.model

data class NaverWebResponse(
        val lastBuildDate: String,
        val total: Int,
        val start: Int,
        val display: Int,
        val items: List<Item>
)