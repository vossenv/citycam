package com.dm.citycam.citycam.search;

public enum SearchFilter {

    ENABLED_ONLY("enabled:true"),
    INCLUDE_DISABLED(""),
    DISABLED_ONLY("enabled:false");

    private final String filter;

    SearchFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

}
