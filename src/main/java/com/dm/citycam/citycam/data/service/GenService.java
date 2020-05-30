package com.dm.citycam.citycam.data.service;

import com.dm.citycam.citycam.config.ApiRequest;
import com.dm.citycam.citycam.config.ListResponse;
import com.dm.citycam.citycam.data.entity.EntityBase;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.TransactionSystemException;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public abstract class GenService<T extends EntityBase, ID> {

    //    private Class<T> persistentClass;
    //       this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    private final EntityManager em;
    private final PagingAndSortingRepository<T, ID> repository;

    public GenService(EntityManager em, PagingAndSortingRepository repository) {
        this.em = em;
        this.repository = repository;
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

    public T findById(ID id) {
        try {
            return repository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("No entity was found for id: " + id.toString());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public List<T> findAll() {
        return findAll(PageRequest.of(0,100));
    }

    public void clear(){
        repository.deleteAll();
    }

    public List<T> findAll(Pageable p) {
        List<T> results = new ArrayList<>();
        repository.findAll(p).iterator().forEachRemaining(results::add);
        return results;
    }

    public ListResponse findAllPaged(ApiRequest request) {
        ListResponse response = new ListResponse(request);
        response.setResultsList(findAll(request.getPageable()));
        response.setRowCount(repository.count());
        return response;
    }
    

    private ID getEntityId(T t) {
        return (ID) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(t);
    }

    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

}

