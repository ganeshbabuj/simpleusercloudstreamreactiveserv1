package com.example.simpleusercloudstreamreactiveserv1.exception;

import com.example.cloudstream.resource.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Mono<ErrorResponse>> handleServiceException(ServiceException e) throws IOException {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(e.getHttpStatus(), e.getMessage());
        return new ResponseEntity<>(Mono.just(errorResponse), e.getHttpStatus());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Mono<ErrorResponse>>  handleNotFoundException(Exception e) throws IOException {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, "Not Found");
        return new ResponseEntity<>(Mono.just(errorResponse), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Mono<ErrorResponse>>  handleException(Exception e) throws IOException {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        return new ResponseEntity<>(Mono.just(errorResponse), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
