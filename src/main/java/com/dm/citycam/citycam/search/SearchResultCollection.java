package com.dm.citycam.citycam.search;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultCollection<T> {

    private long totalCount;
    private List<SearchResult<T>> resultList;
    private double searchTime;

}
