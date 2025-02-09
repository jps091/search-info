package com.search.domain.sse.connection

import com.search.domain.sse.connection.model.UserSseConnection
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class SseConnectionPool: ConnectionPoolIfs<String, UserSseConnection> {
    private val connectionPool: MutableMap<String, UserSseConnection> = ConcurrentHashMap()
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun addSession(uniqueKey: String, session: UserSseConnection) {
        connectionPool[uniqueKey] = session
    }

    override fun getSession(uniqueKey: String): UserSseConnection? {
        return connectionPool[uniqueKey]
    }

    override fun onCompletionCallback(session: UserSseConnection) {
        log.info("call back connection pool completion : {}", session)
        connectionPool.remove(session.uniqueKey)
    }

    fun sendToAll(eventName: String, data: Any){
        connectionPool.values.forEach{ connection ->
            connection.sendMessage(eventName, data)
        }
    }
}