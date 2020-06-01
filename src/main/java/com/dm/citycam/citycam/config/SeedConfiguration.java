package com.dm.citycam.citycam.config;

import com.dm.citycam.citycam.data.entity.CamSource;
import com.dm.citycam.citycam.data.service.CamSourceService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Component
public class SeedConfiguration {

    @Inject
    Environment env;

    @Inject
    CamSourceService cs;

    public void seed() throws URISyntaxException, IOException {

        String seed = env.getProperty("csv.seed");

        if (!Boolean.parseBoolean(seed) && null != seed) {
            System.out.println(("Seed disabled, skipping... "));
            return;
        }

        cs.clear();
        String source = env.getProperty("csv.source");
        Path p = Paths.get(ClassLoader.getSystemResource(source).toURI());
        System.out.println("Beginning CSV load... " + p.toString());
        CsvToBean<CamSource> csvToBean = new CsvToBeanBuilder(Files.newBufferedReader(p))
                .withType(CamSource.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        List<CamSource> cl = csvToBean.parse();
        cs.saveAll(cl);

        System.out.println("Finished CSV load... ");

    }

}
