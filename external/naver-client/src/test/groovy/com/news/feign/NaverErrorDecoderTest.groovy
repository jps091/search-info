package com.news.feign

import com.fasterxml.jackson.databind.ObjectMapper
import com.news.exception.ApiException
import com.news.exception.ErrorType
import com.news.error.NaverErrorResponse
import feign.Request
import feign.Response
import org.springframework.http.HttpStatus
import spock.lang.Specification

class NaverErrorDecoderTest extends Specification {

    ObjectMapper objectMapper = Mock()
    NaverErrorDecoder errorDecoder = new NaverErrorDecoder(objectMapper)

    def "ErrorDecoder에서 에러 발생시 ApiException 예외가 throw 된다."(){
        given:
        def responseBody = Mock(Response.Body)
        def inputStream = new ByteArrayInputStream()
        def response = Response.builder()
                .status(400)
                .request(Request.create(Request.HttpMethod.GET, "testUrl", [:], null as Request.Body, null))
                .body(responseBody)
                .build()

        1 * responseBody.asInputStream() >> inputStream
        1 * objectMapper.readValue(*_) >> new NaverErrorResponse("error", "SE03")

        when:
        errorDecoder.decode(_ as String, response)

        then:
        ApiException e = thrown()
        verifyAll {
            e.errorMessage == "error"
            e.errorType == ErrorType.EXTERNAL_API_ERROR
            e.httpStatus == HttpStatus.BAD_REQUEST
        }
    }
}
