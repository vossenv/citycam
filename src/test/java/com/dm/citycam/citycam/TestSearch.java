package com.dm.citycam.citycam;


import com.dm.citycam.citycam.data.entity.CamSource;
import com.dm.citycam.citycam.data.service.CamSourceService;
import com.dm.citycam.citycam.search.FullTextSearch;
import com.dm.citycam.citycam.search.SearchParameters;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test-full.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestSearch {


    @Inject
    FullTextSearch<CamSource> fullTextSearch;

    @Inject
    CamSourceService camSourceService;

    @PostConstruct
    private void setType() {
        fullTextSearch.setEntityType(CamSource.class);
    }

//    @Test
//    void testNormalQueries() {
//        Arrays.stream(queries).forEach(s ->
//                assertDoesNotThrow(() -> fullTextSearch.search(s))
//        );
//    }

//    @Test
//    void testSearchExceptions() {
//        Arrays.stream(failqueries).forEach(s ->
//                assertThrows(SearchFailedException.class, () -> {System.out.println(s); fullTextSearch.search(s);}));
//    }

//    @Test
//    void testFilter() throws Exception {
//        assertEquals(fullTextSearch.count(""), 17);
//        assertEquals(fullTextSearch.count(new SearchParameters.Builder().enabledOnly().build()),16);
//        assertEquals(fullTextSearch.count("phonybalogna@yourdomain.com"), 1);
//        assertEquals(fullTextSearch.count(
//                new SearchParameters.Builder().withQuery("phonybalogna@yourdomain.com").enabledOnly().build()), 0);
//    }
//
//    @Test
//    void indexNewItem() throws Exception {
//        CamSource c = new CamSource();
//        c.setAuthor("Carag");
//        c.setAnswer("What is the question");
//        c.setQuestion("What is the answer");
//        c = challengeService.save(c);
//        assertEquals(fullTextSearch.search(c.getCamSourceId().toString()).size(), 1);
//        challengeService.deleteById(c.getCamSourceId());
//    }
//
//    @Test
//    void indexDeletedItem() throws Exception {
//        CamSource c = camSourceService.findAll().get(0);
//        SearchParameters params = new SearchParameters(c.getId(),20);
//        assertEquals(fullTextSearch.search(params).size(), 1);
//        camSourceService.deleteById(c.getId());
//        assertEquals(fullTextSearch.search(params).size(), 0);
//
//    }

    @Test
    void testResultCount() throws Exception {
        int count = fullTextSearch.count("");
        assertEquals(count, 827);
    }

    @Test
    void testPagedSearch() throws Exception {
        int total = 0;
        for (int i = 0; i < 15; i++) {
            int pageSize = fullTextSearch.search("",i,100).size();
            if (pageSize == 0) {
                assertEquals(9, i);
                break;
            }
            total += pageSize;
            if (i < 8) assertEquals(100, pageSize);
        }
        assertEquals(total, 827);
    }

    private static final String[] queries = {
            "",
            "a b",
            "a b c c",
            "c a b c",
            "a   b     c",
            "    a   b     c ",
            "    ",
            "\"a b\"",
            "\"a b\" c d",
            "\" \\\" test quoted term in quotes \\\" \"",
            "\" a and b \" \"c and d\"",
            "a AND b",
            "a AND b AND c",
            "AND a AND b AND c",
            "a AND bANDc ANDd",
            "a ANDb c",
            "OR a OR b OR c OR",
            "author : a",
            "author:a",
            "author :a",
            "author :a ",
            "notfield:c",
            "a : \"x y z\"",
            "\"a a\" \"a a\" \"b b\"",
            "\"a a\" AND \"b b\" AND \"c c\"",
            "OR \"a a\" AND b OR \"c c\"",
            "(this OR that) (and this) AND abcd",
            "((a nested) group)",
            "((\"a nested\" planet) group)",
            "this OR (a:keyword in)",
            "this OR (a : keyword in)",
            "this OR (a : \"keyword in\" here)",
            "(a AND b) OR c",
    };

    private static final String[] failqueries = {
            ":: : ",
            "\"",
            ":",
            "a:",
            ":b",
            "C:D:E:F:G",
            "() ()()()",
            "!# $() #&(^ AND &^%????\\\\\\ ",
            "~!#$%^&()_+-/-+<>?:{}|\\]`[';/.,']",
            "\"\\\\\"",
            "\"",
            ":",
            ":::: :",
    };

}
