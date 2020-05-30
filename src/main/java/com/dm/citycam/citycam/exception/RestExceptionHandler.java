package com.dm.citycam.citycam.exception;//package com.dm.umec.exception;
//
//
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.HttpRequestMethodNotSupportedException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//
//@ControllerAdvice
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class RestExceptionHandler extends ResponseEntityExceptionHandler {
//
//    private final static String INVALID_PAGING_PARAMETER = "You have entered an invalid paging parameter";
//    private final static String INVALID_ENTITY_ID = "You have entered an invalid entity id (UUID format required)";
//    private final static String ENTITY_NOT_FOUND = "The specified entity does not exist";
//    private final static String INVALID_PARAMETER = "You have entered an invalid query parameter";
//    private final static String UNSUPPORTED_MESSAGE = "The request method is not supported";
//    private final static String UNKNOWN_FAILURE = "Request failed for unknown reason";
//
//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity<Object> handleInvalidEntityIdException(Exception e) {
//        return new ScifiError(INVALID_ENTITY_ID, HttpStatus.BAD_REQUEST, e).toResponse();
//    }
//
//    // Entity CRUD
////    @ExceptionHandler(InvalidEntityIdException.class)
////    protected ResponseEntity<Object> handleInvalidEntityIdException(InvalidEntityIdException e) {
////        return new TeamQueryError(INVALID_ENTITY_ID, HttpStatus.BAD_REQUEST, e).toResponse();
////    }
////
////    @ExceptionHandler(EntityNotFoundException.class)
////    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
////        return new TeamQueryError(ENTITY_NOT_FOUND, HttpStatus.NOT_FOUND, e).toResponse();
////    }
//
//    @ExceptionHandler({ SearchFailedException.class})
//    protected ResponseEntity<Object> handleFailedException(ScifiException e) {
//        return new ScifiError(UNKNOWN_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR, e).toResponse();
//    }
//
//    // Paging
//    @ExceptionHandler(InvalidParameterException.class)
//    protected ResponseEntity<Object> handleInvalidParameterException(InvalidParameterException e) {
//        return new ScifiError(INVALID_PAGING_PARAMETER, HttpStatus.BAD_REQUEST, e).toResponse();
//    }
//
//    // Controller
//    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
//    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e) {
//        return new ScifiError(INVALID_PARAMETER, HttpStatus.BAD_REQUEST, e).toResponse();
//    }
//
//    // Request method
//    @Override
//    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
//            HttpRequestMethodNotSupportedException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        return new ScifiError(UNSUPPORTED_MESSAGE, HttpStatus.BAD_REQUEST, e).toResponse();
//    }
//
//    // Field error handling
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
//            return new ScifiError(UNSUPPORTED_MESSAGE, HttpStatus.UNPROCESSABLE_ENTITY, e).toResponse();
//    }
//}
//
//
//
//
////    @Override
////    protected ResponseEntity<Object> handleNoHandlerFoundException(
////            NoHandlerFoundException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
////        List<String> errorDetails = new ArrayList<>();
////        errorDetails.add("The URL is invalid: " + e.getHeaders().getHost() + e.getRequestURL());
////        return buildResponseEntity(RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, errorDetails);
////    }
//
////    @ExceptionHandler
////    public String constraintViolationHandler(ConstraintViolationException ex) {
////        return ex.getConstraintViolations().iterator().next()
////                .getErrorMessage();
////    }
//
////    @ExceptionHandler(ConstraintViolationException.class)
////    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
////        return new ResponseEntity<>("not valid due to validation error: " + e.getErrorMessage(), HttpStatus.BAD_REQUEST);
////    }