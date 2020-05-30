package com.dm.citycam.citycam.data.service;

import com.dm.citycam.citycam.data.entity.CamSource;
import com.dm.citycam.citycam.data.repository.CamSourceRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.UUID;

@Component
public class CamSourceService extends GenService<CamSource, UUID> {
    CamSourceService(EntityManager em, CamSourceRepository re){
        super(em, re);
    }
}
