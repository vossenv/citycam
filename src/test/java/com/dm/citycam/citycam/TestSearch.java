package com.dm.citycam.citycam;


import com.dm.citycam.citycam.config.RequestInfo;
import com.dm.citycam.citycam.data.service.CamSourceService;
import com.dm.citycam.citycam.exception.SearchFailedException;
import com.dm.citycam.citycam.search.SearchFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test-full.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestSearch {

    @Inject
    private CamSourceService cs;

    @Test
    void TestFind() throws SearchFailedException {

        RequestInfo ri = new RequestInfo();

        ri.setQuery("");
        ri.setIncDisabled(true);

        SearchFilter z = SearchFilter.ENABLED_ONLY;
        String g = z.getFilter();
        //cs.search(ri);

        System.out.println();
    }

}
