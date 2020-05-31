package com.dm.citycam.citycam.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;


@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final static String INVALID_PAGING_PARAMETER = "You have entered an invalid paging parameter";
    private final static String INVALID_ENTITY_ID = "You have entered an invalid entity id (UUID format required)";
    private final static String ENTITY_NOT_FOUND = "The specified entity does not exist";
    private final static String INVALID_PARAMETER = "You have entered an invalid qargument";
    private final static String UNSUPPORTED_MESSAGE = "The request method is not supported";
    private final static String UNKNOWN_FAILURE = "Request failed for unknown reason";
    private final static String BAD_ENTITY_FIELDS = "Could not create entity";

    // Paging
    @ExceptionHandler(InvalidParameterException.class)
    protected ResponseEntity<Object> handleInvalidParameterException(InvalidParameterException e) {
        return new APIError(INVALID_PAGING_PARAMETER, HttpStatus.BAD_REQUEST, e).toResponse();
    }

    // Controller
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return new APIError(INVALID_PARAMETER, HttpStatus.BAD_REQUEST, e).toResponse();
    }

    // Request method
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new APIError(UNSUPPORTED_MESSAGE, HttpStatus.BAD_REQUEST, e).toResponse();
    }

    // Field error handling
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new APIError(UNSUPPORTED_MESSAGE, HttpStatus.UNPROCESSABLE_ENTITY, e).toResponse();
    }

    @Override
    protected ResponseEntity<Object>  handleNoHandlerFoundException(
            NoHandlerFoundException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new APIError(UNSUPPORTED_MESSAGE, HttpStatus.UNPROCESSABLE_ENTITY, e).toResponse();
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpHeaders headers, HttpStatus status, WebRequest request)  {
        return new APIError(BAD_ENTITY_FIELDS, HttpStatus.UNPROCESSABLE_ENTITY, e).toResponse();
    }

    // Entity CRUD
    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        return new APIError(ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND, e).toResponse();
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleInvalidEntityIdException(Exception e) {
        return new APIError(UNKNOWN_FAILURE, HttpStatus.BAD_REQUEST, e).toResponse();
    }

}