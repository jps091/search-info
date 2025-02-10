package com.search.config.apikey

interface NaverApiKeyManagerIfs {
    fun getCurrentApiKey(): NaverProperties.Header
    fun recordApiCall()
}