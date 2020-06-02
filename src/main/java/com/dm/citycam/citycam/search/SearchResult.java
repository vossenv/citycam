package com.dm.citycam.citycam.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult<T> {

    private long resultTotalCount;
    private List<T> resultList;
    private Double searchTime;
}
