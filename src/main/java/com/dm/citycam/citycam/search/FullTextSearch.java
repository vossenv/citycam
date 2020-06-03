package com.dm.citycam.citycam.search;

import com.dm.citycam.citycam.exception.SearchFailedException;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.springframework.data.domain.PageRequest;
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

    public List<T> search(String query) throws SearchFailedException {
        return search(SearchParameters.fromQuery(query));
    }

    public List<T> search(String query, int page, int size) throws SearchFailedException {
        return search(SearchParameters.fromQuery(query).withPageable(PageRequest.of(page, size)));
    }

    public int count(String query) throws SearchFailedException {
        return count(SearchParameters.fromQuery(query));
    }

    public List<T> search(SearchParameters sp) throws SearchFailedException {
        return parseQuery(sp).getResultList();
    }

    public int count(SearchParameters sp) throws SearchFailedException {
        return parseQuery(sp).getResultSize();
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
}


