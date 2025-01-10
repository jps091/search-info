package com.search.feign

import com.fasterxml.jackson.databind.ObjectMapper
import com.search.error.ErrorType
import com.search.error.KakaoErrorResponse
import com.search.exception.ApiException
import feign.Response
import feign.codec.ErrorDecoder
import org.slf4j.LoggerFactory
import java.io.IOException
import java.lang.Exception
import java.nio.charset.StandardCharsets

class KakaoErrorDecoder(private val objectMapper: ObjectMapper) : ErrorDecoder {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun decode(methodKey: String, response: Response): Exception {
        try{
            val body = String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8)
            val errorResponse = objectMapper.readValue(body, KakaoErrorResponse::class.java)
            throw ApiException(errorResponse.message, ErrorType.EXTERNAL_API_ERROR, response.status())
        }catch (e: IOException){
            log.error(
                    "[Kakao] 에러 메세지 파싱 에러 code={}, request={}, methodKey={}, errorMessage={}",
                    response.status(), response.request(), methodKey, e.message
            )
            throw ApiException("카카오 메세지 파싱 에러", ErrorType.EXTERNAL_API_ERROR, response.status())
        }
    }
}