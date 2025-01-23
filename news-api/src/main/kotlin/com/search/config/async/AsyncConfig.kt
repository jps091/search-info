package com.search.config.async

import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.lang.reflect.Method
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer{
    private val log = LoggerFactory.getLogger(this::class.java)
    @Bean("event-Executor")
    override fun getAsyncExecutor(): Executor {
        //val coreCount = Runtime.getRuntime().availableProcessors()
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 2
            maxPoolSize = 5
            queueCapacity = 5
            keepAliveSeconds = 30
            setWaitForTasksToCompleteOnShutdown(true)
            setAwaitTerminationSeconds(10)
            setThreadNamePrefix("event-")
            initialize()
        }
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return CustomAsyncExceptionHandler()
    }

    private inner class CustomAsyncExceptionHandler : AsyncUncaughtExceptionHandler{
        override fun handleUncaughtException(ex: Throwable, method: Method, vararg params: Any?) {
            log.error("Failed to execute ${method.name}: ${ex.message}")
            params.forEach {
                param -> log.error("Parameter value = $param")
            }
        }
    }
}