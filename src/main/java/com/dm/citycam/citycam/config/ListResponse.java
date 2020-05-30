package com.dm.citycam.citycam.config;


import io.vavr.API;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.min;


@Data
public class ListResponse {
    private ApiRequest request;
    private long rowCount;
    private Double searchTime;
    private List<?> resultsList;

    public ListResponse(ApiRequest request) {
        this.request = request;
    }

    public ResponseEntity getResponse(Class type, Class dest) throws EntityNotFoundException {

        CollectionModel responseBody = new CollectionModel(
                resultsList.stream()
                        .map(API.unchecked(o ->
                                dest.getConstructor(type)
                                        .newInstance(type.cast(o))))
                        .collect(Collectors.toList()));

        return prepareResponse(responseBody);

    }

    private ResponseEntity prepareResponse(CollectionModel body) throws EntityNotFoundException {

        HttpHeaders headers = new HttpHeaders();
        int pageCount = (int) Math.ceil((double) this.getRowCount() / (double) request.getSize());
        int next = min((request.getPage() + 2), pageCount);
        int prev = Integer.max(request.getPage(), 1);

        if (pageCount > 1 && request.getPage() + 1 > pageCount)
            throw new EntityNotFoundException(String.format("Requested page (%d) does not exist", request.getPage()));

        headers.add("Page-Size", request.getSize().toString());
        headers.add("Current-Page", String.valueOf(1 + request.getPage()));
        headers.add("Previous-Page", String.valueOf(prev));
        headers.add("Next-Page", String.valueOf(next));
        headers.add("Result-Count", String.valueOf(this.rowCount));
        headers.add("Search-Time-Seconds", String.valueOf(this.searchTime));
        headers.add("Total-Time-Seconds", String.valueOf((System.nanoTime() - request.getRequestTime()) * 1.0e-9));
        headers.add("Original-Query", request.getQuery());
        headers.add("Page-Count", String.valueOf(pageCount));

        String lastCall = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();

        body.add(new Link(lastCall, "self"));

        if (pageCount > 1) {
            body.add(new Link(lastCall.replaceAll("(page=)\\d*", "page=" + next), "next"));
            body.add(new Link(lastCall.replaceAll("(page=)\\d*", "page=" + prev), "previous"));
        }

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}


