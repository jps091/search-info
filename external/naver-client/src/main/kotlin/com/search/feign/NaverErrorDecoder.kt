package com.search.feign

import com.fasterxml.jackson.databind.ObjectMapper
import com.search.error.ErrorType
import com.search.error.NaverErrorResponse
import com.search.exception.ApiException
import feign.Response
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import java.io.IOException
import java.lang.Exception
import java.nio.charset.StandardCharsets

class NaverErrorDecoder(private val objectMapper: ObjectMapper) : ErrorDecoder {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun decode(methodKey: String, response: Response): Exception {
        try{
            val body = String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8)
            val errorResponse = objectMapper.readValue(body, NaverErrorResponse::class.java)
            throw ApiException(errorResponse.errorMessage, ErrorType.EXTERNAL_API_ERROR, response.status())
        } catch (e: IOException){
            log.error(
                    "[Naver] 에러 메세지 파싱 에러 code={}, request={}, methodKey={}, errorMessage={}",
                    response.status(), response.request(), methodKey, e.message
            )
            throw ApiException("네이버 메세지 파싱 에러", ErrorType.EXTERNAL_API_ERROR, response.status())
        }
    }
}