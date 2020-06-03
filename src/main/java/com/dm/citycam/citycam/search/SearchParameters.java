package com.dm.citycam.citycam.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchParameters {

    private String query = "";
    private Pageable pageable = PageRequest.of(0, 100);
    private SearchFilter filter = SearchFilter.ENABLED_ONLY;
    private int precision = 3;

    public SearchParameters(String query) {
        this.query = query;
    }

    public static SearchParameters fromQuery(String query) {
        return new SearchParameters(query);
    }

    public SearchParameters withQuery(String query) {
        this.query = query;
        return this;
    }

    public SearchParameters withPageable(Pageable pageable) {
        this.pageable = pageable;
        return this;
    }

    public SearchParameters withFilter(SearchFilter filter) {
        this.filter = filter;
        return this;
    }

    public SearchParameters withPrecision(int precision) {
        this.precision = precision;
        return this;
    }

    public SearchParameters withPageSize(int size) {
        this.pageable = PageRequest.of(this.pageable.getPageNumber(), size);
        return this;
    }

    public SearchParameters withPageNumber(int number) {
        this.pageable = PageRequest.of(number, this.pageable.getPageSize());
        return this;
    }
}