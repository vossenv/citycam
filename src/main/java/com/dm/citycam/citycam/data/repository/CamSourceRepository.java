package com.dm.citycam.citycam.data.repository;


import com.dm.citycam.citycam.data.entity.CamSource;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface CamSourceRepository extends PagingAndSortingRepository<CamSource, UUID> {

    CamSource findCamSourceByTitle(String title);

}



