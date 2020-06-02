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

    private String query;
    private Pageable pageable;
    private SearchFilter filter;
    private int precision;

    public static class Create {
        private String query = "";
        private Pageable pageable = PageRequest.of(0, 100);
        private SearchFilter filter = SearchFilter.ENABLED_ONLY;
        private int precision = 3;

        public Create() {}

        public Create(String query) {
            this.query = query;
        }

        public Create pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public Create query(String query) {
            this.query = query;
            return this;
        }

        public Create filter(SearchFilter filter) {
            this.filter = filter;
            return this;
        }

        public Create precision(int precision) {
            this.precision = precision;
            return this;
        }

        public Create pageSize(int size){
            this.pageable = PageRequest.of(0, size);
            return this;
        }

        public SearchParameters get() {

            SearchParameters s = new SearchParameters();
            s.filter = this.filter;
            s.precision = this.precision;
            s.pageable = this.pageable;
            s.query = this.query;
            return s;
        }
    }
}
