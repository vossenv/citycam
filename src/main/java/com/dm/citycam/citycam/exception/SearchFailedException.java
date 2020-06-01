package com.dm.citycam.citycam.exception;


public class SearchFailedException extends GenException {
    public SearchFailedException(Object... errors) {
        super(errors);
    }

    public SearchFailedException(StackTraceElement[] trace, Object... errors) {
        super(trace, errors);
    }
}