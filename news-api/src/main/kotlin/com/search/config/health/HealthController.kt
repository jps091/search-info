package com.search.config.health

import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api")
class HealthController {

    private val logger : org.slf4j.Logger = LoggerFactory.getLogger(this.javaClass)
    @GetMapping("/health")
    fun health(){
        logger.info("health call")
    }
}