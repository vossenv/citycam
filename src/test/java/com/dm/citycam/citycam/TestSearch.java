package com.dm.citycam.citycam;


import com.dm.citycam.citycam.data.entity.CamSource;
import com.dm.citycam.citycam.data.service.CamSourceService;
import com.dm.citycam.citycam.exception.SearchFailedException;
import com.dm.citycam.citycam.search.FullTextSearch;
import com.dm.citycam.citycam.search.SearchFilter;
import com.dm.citycam.citycam.search.SearchParameters;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testNormalQueries() {
        Arrays.stream(queries).forEach(s ->
                assertDoesNotThrow(() -> fullTextSearch.search(s))
        );
    }

    @Test
    void testSearchExceptions() {
        Arrays.stream(failqueries).forEach(s ->
                assertThrows(SearchFailedException.class, () ->
                {
                    System.out.println(s);
                    fullTextSearch.search(s);
                }));
    }

    @Test
    void testFilter() throws Exception {
        assertEquals(fullTextSearch.count(SearchParameters.fromQuery("*").withFilter(SearchFilter.DISABLED_ONLY)), 2);
        assertEquals(fullTextSearch.count(SearchParameters.fromQuery("disabled").withFilter(SearchFilter.DISABLED_ONLY)), 2);
        assertEquals(fullTextSearch.count("disabled"), 0);
    }

    @Test
    void indexNewItem() throws Exception {
        CamSource c = new CamSource();
        c.setId("123456789");
        c = camSourceService.save(c);
        assertEquals(fullTextSearch.search(SearchParameters.fromQuery(c.getId())).size(), 1);
        camSourceService.deleteById(c.getId());
    }

    @Test
    void indexDeletedItem() throws Exception {
        CamSource c = camSourceService.findAll().get(0);
        SearchParameters params = SearchParameters.fromQuery(c.getId()).withPrecision(20);
        assertEquals(fullTextSearch.search(params).size(), 1);
        camSourceService.deleteById(c.getId());
        assertEquals(fullTextSearch.search(params).size(), 0);
    }

    @Test
    void testResultCount() throws Exception {
        int count = fullTextSearch.count("");
        assertEquals(count, 827);
    }

    @Test
    void testURL() throws Exception {

        CamSource s = camSourceService.findById("C621");
        SearchParameters p = SearchParameters.fromQuery("https://video.dot.state.mn.us/video/image/metro/C621")
                .withFilter(SearchFilter.INCLUDE_DISABLED).withPrecision(100);
        Object j = fullTextSearch.search(p);
        Object q = fullTextSearch.search("video/image/metro/C621");

        System.out.println();
    }

    @Test
    void testPagedSearch() throws Exception {
        int pageSize = 100;
        int count = fullTextSearch.count("*");
        int expectPages = (int) Math.ceil((double) count / (double) pageSize);

        int total = 0;
        for (int i = 0; i <= expectPages; i++) {
            int resultsSize = fullTextSearch.search("*", i, pageSize).size();
            if (resultsSize == 0) {
                assertEquals(expectPages, i);
                break;
            }
            total += resultsSize;
            if (i < expectPages - 1) assertEquals(pageSize, resultsSize);
        }
        assertEquals(total, count);
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
