package com.dm.citycam.citycam.exception;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class GenException extends Exception {
    private List<String> errorList = new ArrayList<>();
    List<StackTraceElement[]> traceList = new ArrayList<>();

    public GenException(Object... args) {
        super();
        addExceptions(args);
    }

    public void addExceptions(Object... args) {
        for (Object o : args) processObject(o);
    }

    @Override
    public String getMessage() {
        return errorList.stream().collect(Collectors.joining(", "));
    }

    private void processObject(Object o) {
        if (o instanceof List || o instanceof Set) {
            ((Collection) o).forEach(this::processObject);
        } else if (o instanceof Map) {
            ((Map) o).forEach((k, v) -> errorList.add(k.toString() + " = " + v.toString()));
        } else if (o instanceof Exception) {
            traceList.add(((Exception) o).getStackTrace());
            errorList.add(o.getClass().getSimpleName()
                    + " - " + ExceptionUtils.getRootCauseMessage((Exception) o));
        } else if (o instanceof StackTraceElement[]) {
            traceList.add((StackTraceElement[]) o);
            this.setStackTrace((StackTraceElement[]) o);
        } else if (o instanceof Object[]) {
            for (Object e : (Object[]) o) processObject(e);
        } else {
            errorList.add(o.toString());
        }

    }

}
