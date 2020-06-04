package com.dm.citycam.citycam.search;

import com.dm.citycam.citycam.exception.SearchFailedException;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.transform.ResultTransformer;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Repository
@Transactional
public class FullTextSearch<T> {

    private Class<?> entityType;
    private final FullTextEntityManager fullTextEm;
    private MultiFieldQueryParser queryParser;


    @Inject
    public FullTextSearch(EntityManagerFactory emf) {
        this.fullTextEm = Search.getFullTextEntityManager(emf.createEntityManager());
    }

    public SearchResultCollection<T> search(String query) throws SearchFailedException {
        return search(SearchParameters.fromQuery(query));
    }

    public SearchResultCollection<T> search() throws SearchFailedException {
        return search(SearchParameters.fromQuery("*"));
    }

    public int count() throws SearchFailedException {
        return count(SearchParameters.fromQuery("*"));
    }

    public int count(String query) throws SearchFailedException {
        return count(SearchParameters.fromQuery(query));
    }

    public int count(SearchParameters sp) throws SearchFailedException {
        return parseQuery(sp).getResultSize();
    }


    public SearchResultCollection<T> search(SearchParameters sp) throws SearchFailedException {
        double time = (double) System.nanoTime();
        FullTextQuery q = parseQuery(sp);
        return new SearchResultCollection<T>(
                q.getResultSize(),
                q.getResultList(),
                (System.nanoTime() - time) * 1.0e-9);
    }


    private FullTextQuery parseQuery(SearchParameters parameters) throws SearchFailedException {

        String query = parameters.getQuery();
        String filter = parameters.getFilter().getFilter();
        Pageable p = parameters.getPageable();
        Assert.notNull(query, "Query must not be null");

        try {
            query = new LanguageProcessor(parameters.getPrecision()).format(query);
            query = (!filter.trim().isEmpty()) ? String.format("(%s) AND %s", query, filter) : query;

            Query r = queryParser.parse(query);
            FullTextQuery jpaQuery = fullTextEm.createFullTextQuery(r, entityType);

            FullTextQuery q = jpaQuery.setMaxResults(p.getPageSize()).setFirstResult(p.getPageNumber() * p.getPageSize());
            q.setProjection(FullTextQuery.SCORE, FullTextQuery.THIS);
            q.setResultTransformer(new SearchResultsTransformer(parameters.getRelevance()));
            return q;

        } catch (ParseException e) {
            throw new SearchFailedException(e.getStackTrace(),
                    e.getMessage().split("\\v")[0].replace("~", ""));
        }
    }


    public void setEntityType(Class<?> entityType) {
        Assert.notNull(entityType, "Entity type must not be null");
        queryParser = new MultiFieldQueryParser(getEntityFields(entityType), fullTextEm.getSearchFactory().getAnalyzer(entityType));
        queryParser.setAllowLeadingWildcard(true);
        this.entityType = entityType;
    }

    private String[] getEntityFields(Class<?> c) {
        Set<String> fieldNames = new HashSet<>();
        while (c != null) {
            stream(c.getDeclaredFields()).map(Field::getName).forEach(fieldNames::add);
            c = c.getSuperclass();
        }
        return fieldNames.toArray(new String[0]);
    }

    private class SearchResultsTransformer implements ResultTransformer {

        private final double cutoff;
        private double max;

        public Predicate<SearchResult<T>> aboveCutoff() {
            return s -> s.getScore() / max >= cutoff;
        }

        public SearchResultsTransformer(double cutoff) {
            this.cutoff = cutoff;
        }

        @Override
        public Object transformTuple(Object[] tuple, String[] aliases) {
            return new SearchResult<>(((Float) tuple[0]).doubleValue(), (T) tuple[1]);
        }

        @Override
        public List<SearchResult<T>> transformList(List collection) {
            List<SearchResult<T>> c = (List<SearchResult<T>>) collection;
            if (collection.size() > 1) {
                max = c.get(0).getScore();
                c = c.stream().filter(aboveCutoff()).collect(Collectors.toList());
            }
            return c;
        }
    }

}


