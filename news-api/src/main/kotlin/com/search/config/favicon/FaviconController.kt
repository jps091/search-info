package com.search.config.favicon

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FaviconController {
    @RequestMapping("favicon.ico")
    fun favicon(): ResponseEntity<Void> {
        return ResponseEntity.noContent().build()
    }
}