package com.search.domain.sse.connection

interface ConnectionPoolIfs<T, R> {
    fun addSession(uniqueKey: T, session: R)
    fun getSession(uniqueKey: T): R?
    fun onCompletionCallback(session: R)
}