package com.dm.citycam.citycam.data.service;

import com.dm.citycam.citycam.data.entity.CamSource;
import com.dm.citycam.citycam.data.repository.CamSourceRepository;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

@Component
public class CamSourceService extends GenService<CamSource, String> {

    CamSourceRepository cr;

    CamSourceService(EntityManager em, CamSourceRepository re) {
        super(em, re);
        this.cr = re;
    }

    public CamSource findByTitle(String title) {

        try {
            CamSource source = cr.findCamSourceByTitle(title);
            if (null == source) {
                throw new EntityNotFoundException("No entity was found for id: " + title);
            }
            return source;
        } catch (InvalidDataAccessApiUsageException e) {
            throw new IllegalArgumentException(ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public CamSource updateExisting(CamSource source) {
        try {
            CamSource existing = findByTitle(source.getTitle());
            source.setId(existing.getId());
        } catch (EntityNotFoundException e) {
            // Do nothing
        }
        return source;
    }

}
