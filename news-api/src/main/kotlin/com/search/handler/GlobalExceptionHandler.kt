package com.search.handler

import com.search.error.ErrorResponse
import com.search.error.ErrorType
import com.search.exception.ApiException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApiException(e: ApiException): ResponseEntity<ErrorResponse>{
        log.error("Api Exception occurred. message={}, className={}", e.errorMessage, e.javaClass.name)
        return ResponseEntity.status(e.httpStatusCode)
                .body(ErrorResponse(e.errorMessage, e.errorType))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse>{
        log.error("Exception occurred. message={}, className={}", e.message, e.javaClass.name, e)
        return ResponseEntity.status(500)
                .body(ErrorResponse(ErrorType.UNKNOWN.description, ErrorType.UNKNOWN))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(e: NoResourceFoundException): ResponseEntity<ErrorResponse>{
        log.error("NoResourceFound Exception occurred. message={}, className={}", e.message, e.javaClass.name)
        return ResponseEntity.status(400)
                .body(ErrorResponse(ErrorType.NO_RESOURCE.description, ErrorType.NO_RESOURCE))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException): ResponseEntity<ErrorResponse>{
        log.error("MissingServletRequestParameter Exception occurred. parameterName={}, message={}", e.parameterName, e.message)
        return ResponseEntity.status(400)
                .body(ErrorResponse(ErrorType.INVALID_PARAMETER.description, ErrorType.INVALID_PARAMETER))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse>{
        log.error("MethodArgumentTypeMismatch Exception occurred. message={}", e.message)
        return ResponseEntity.status(400)
                .body(ErrorResponse(ErrorType.INVALID_PARAMETER.description, ErrorType.INVALID_PARAMETER))
    }

    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): ResponseEntity<ErrorResponse>{
        log.error("Bind Exception occurred. message={}, className={}", e.message, e.javaClass.name)
        return ResponseEntity.status(400)
                .body(ErrorResponse(createMessage(e), ErrorType.INVALID_PARAMETER))
    }

    private fun createMessage(e: BindException): String {
        val fieldError = e.fieldError
        if(fieldError != null && !fieldError.defaultMessage.isNullOrBlank()){
            return fieldError.defaultMessage!!
        }

        val errorFields = e.fieldErrors.joinToString(", ") { it.field }

        return "$errorFields 값들이 정확하지 않습니다."
    }
}