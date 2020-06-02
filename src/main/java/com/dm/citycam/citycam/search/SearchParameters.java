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
    private int fuzzyLen = 3;

    public SearchParameters(String query, int page, int limit){
        this.query = query;
        this.pageable = PageRequest.of(page, limit);
    }

}
