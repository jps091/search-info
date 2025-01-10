package com.news.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiException extends RuntimeException{
    private final String errorMessage;
    private final ErrorType errorType;
    private final HttpStatus httpStatus;
}
