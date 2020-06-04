package com.dm.citycam.citycam.search;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult<T> {

    private double score;
    private T entity;
}
