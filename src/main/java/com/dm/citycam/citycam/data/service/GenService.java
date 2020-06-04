package com.dm.citycam.citycam.data.service;

import com.dm.citycam.citycam.exception.SearchFailedException;
import com.dm.citycam.citycam.search.FullTextSearch;
import com.dm.citycam.citycam.search.SearchParameters;
import com.dm.citycam.citycam.search.SearchResultCollection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.TransactionSystemException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public abstract class GenService<T, ID> {

    private final EntityManager em;
    private final PagingAndSortingRepository<T, ID> repository;
    private final Class<T> persistentClass;

    public GenService(EntityManager em, PagingAndSortingRepository<T, ID> repository) {
        this.em = em;
        this.repository = repository;
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Inject
    private FullTextSearch<T> fullTextSearch;

    @PostConstruct
    void setClass() {
        this.fullTextSearch.setEntityType(persistentClass);
    }


//    public List<T> basicSearch(String query) throws SearchFailedException {

//    }

    public SearchResultCollection<T> search(SearchParameters searchParameters) throws SearchFailedException {
        try {
            return fullTextSearch.search(searchParameters);
        } catch (Exception e) {
            throw (e instanceof SearchFailedException) ? (SearchFailedException) e
                    : new SearchFailedException(ExceptionUtils.getRootCauseMessage(e));
        }
    }


    public T save(T t) {
        try {
            return findById(getEntityId(repository.save(t)));
        } catch (TransactionSystemException e) {
            Throwable th = ExceptionUtils.getRootCause(e);
            if (ExceptionUtils.getRootCause(e) instanceof ConstraintViolationException) {
                throw new ConstraintViolationException(((ConstraintViolationException) th).getConstraintViolations());
            } else {
                throw new PersistenceException(th.getMessage());
            }
        }
    }

    public void deleteById(ID id) {
        try {
            repository.deleteById(id);
            if (existsById(id)) throw new PersistenceException("Delete failed for unknown reasons!");
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException(ExceptionUtils.getRootCauseMessage(e));
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public T findById(ID id) {
        try {
            return repository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("No entity was found for id: " + id.toString());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public T patch(Map<String, Object> fields, ID id) throws IllegalAccessException {
        return patch(fields, id, true);
    }

    public T patch(Map<String, Object> fields, ID id, boolean force) throws IllegalAccessException {

        T entity = findById(id);

        for (Field f : this.persistentClass.getDeclaredFields()) {
            f.setAccessible(true);
            Object v = fields.remove(f.getName());
            if (null != v) {
                f.set(entity, v);
            }
        }

        if (force && fields.size() != 0) {
            throw new IllegalArgumentException(
                    String.format("Illegal fields for type %s: %s",
                            this.persistentClass.getSimpleName(), fields.keySet().toString()));
        }

        return save(entity);
    }

    public List<T> saveAll(List<T> toSave) {
        return (List<T>) repository.saveAll(toSave);
    }

    public List<T> findAll() {
        return findAll(PageRequest.of(0, 100));
    }

    public void clear() {
        repository.deleteAll();
    }

    public long count() {
        return repository.count();
    }

    public List<T> findAll(Pageable p) {
        List<T> results = new ArrayList<>();
        repository.findAll(p).iterator().forEachRemaining(results::add);
        return results;
    }

    private ID getEntityId(T t) {
        return (ID) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(t);
    }

    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

}

