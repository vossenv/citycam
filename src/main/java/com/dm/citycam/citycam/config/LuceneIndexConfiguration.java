package com.dm.citycam.citycam.config;


import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.net.URISyntaxException;


@Configuration
public class LuceneIndexConfiguration {

    private final EntityManager entityManager;
    private final SeedConfiguration seedConfiguration;

    @Inject
    public LuceneIndexConfiguration(final EntityManagerFactory entityManagerFactory, SeedConfiguration seedConfiguration) {
        this.entityManager = entityManagerFactory.createEntityManager();
        this.seedConfiguration = seedConfiguration;

    }

    @PostConstruct
    public void onApplicationEvent() throws InterruptedException, IOException, URISyntaxException {
        FullTextEntityManager fullTextEntityManager =  Search.getFullTextEntityManager(entityManager);
        fullTextEntityManager.createIndexer().startAndWait();
        seedConfiguration.seed();
    }
}