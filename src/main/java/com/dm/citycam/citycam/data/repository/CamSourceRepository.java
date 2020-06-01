package com.dm.citycam.citycam.data.repository;


import com.dm.citycam.citycam.data.entity.CamSource;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CamSourceRepository extends PagingAndSortingRepository<CamSource, String> {

    CamSource findCamSourceByTitle(String title);

}



