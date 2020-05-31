package com.dm.citycam.citycam.config;


import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;

import static java.lang.Math.min;


@Data
public class ListResponseHeaders {

    HttpHeaders headers = new HttpHeaders();

    public ListResponseHeaders(ApiRequest request, long rowCount) throws EntityNotFoundException {

        int pageCount = (int) Math.ceil((double) rowCount / (double) request.getSize());
        int next = min((request.getPage() + 2), pageCount);
        int prev = Integer.max(request.getPage(), 1);

        if (pageCount > 1 && request.getPage() + 1 > pageCount)
            throw new EntityNotFoundException(String.format("Requested page (%d) does not exist", request.getPage()));

        String lastCall = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();

        headers.add("Page-Size", request.getSize().toString());
        headers.add("Current-Page", String.valueOf(1 + request.getPage()));
        headers.add("Previous-Page", String.valueOf(prev));
        headers.add("Next-Page", String.valueOf(next));
        headers.add("Result-Count", String.valueOf(rowCount));
        headers.add("Page-Count", String.valueOf(pageCount));
        headers.add("Last-Call", lastCall);

    }

    public static HttpHeaders from(ApiRequest request, long rowCount){
        return new ListResponseHeaders(request, rowCount).getHeaders();
    }
}


