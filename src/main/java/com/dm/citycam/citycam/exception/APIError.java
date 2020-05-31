package com.dm.citycam.citycam.exception;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class APIError {
    @JsonIgnore
    private HttpStatus status;
    @JsonIgnore
    private final static String FIELD_ERROR_DETAIL = "Error: field '%s' - value '%s' is invalid: %s";
    @JsonIgnore
    private final static String FIELD_ERROR_DESCRIPTION = "Field errors were encountered during the requests";

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private String statusString;
    private String errorMessage;
    private List<String> errorList = new ArrayList<>();

    public APIError(String errorMessage, HttpStatus status, Exception e) {

        this.errorMessage = errorMessage;
        setStatus(status);
        if (e instanceof GenException) {
            this.errorList = ((GenException) e).getErrorList();
        } else if (e instanceof MethodArgumentNotValidException) {
            this.errorList = getBindingErrors((MethodArgumentNotValidException) e);
        } else {
            this.errorList.add(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public ResponseEntity toResponse() {
        return ResponseEntity.status(this.status).body(this);
    }

    private List<String> getBindingErrors(MethodArgumentNotValidException e) {
        return e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(f -> (FieldError) f)
                .map(f -> String.format(FIELD_ERROR_DETAIL,
                        f.getField(),
                        (f.getRejectedValue() != null) ? f.getRejectedValue().toString() : "null",
                        f.getDefaultMessage())).collect(Collectors.toList());
    }

    private void setStatus(HttpStatus status) {
        this.status = status;
        this.statusString = status.value() + " " + status.getReasonPhrase();
    }

}
