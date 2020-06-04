package com.dm.citycam.citycam;


import com.dm.citycam.citycam.data.entity.CamSource;
import com.dm.citycam.citycam.data.service.CamSourceService;
import com.dm.citycam.citycam.exception.SearchFailedException;
import com.dm.citycam.citycam.search.FullTextSearch;
import com.dm.citycam.citycam.search.SearchFilter;
import com.dm.citycam.citycam.search.SearchParameters;
import com.dm.citycam.citycam.search.SearchResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
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
    FullTextSearch<CamSource> fts;

    @Inject
    CamSourceService cs;

    @PostConstruct
    private void setType() {
        fts.setEntityType(CamSource.class);
    }

    @Test
    void testNormalQueries() {
        Arrays.stream(queries).forEach(s ->
                assertDoesNotThrow(() -> fts.search(s))
        );
    }

    @Test
    void testSearchExceptions() {
        Arrays.stream(failqueries).forEach(s ->
                assertThrows(SearchFailedException.class, () ->
                {
                    System.out.println(s);
                    fts.search(s);
                }));
    }

    @Test
    void testFilter() throws Exception {
        assertEquals(fts.count(SearchParameters.fromQuery("*").withFilter(SearchFilter.DISABLED_ONLY)), 2);
        assertEquals(fts.count(SearchParameters.fromQuery("disabled").withFilter(SearchFilter.DISABLED_ONLY)), 2);
        assertEquals(fts.count("disabled"), 0);
    }

    @Test
    void indexNewItem() throws Exception {
        CamSource c = new CamSource();
        c.setId("123456789");
        c = cs.save(c);
        assertEquals(fts.count(SearchParameters.fromQuery(c.getId())), 1);
        cs.deleteById(c.getId());
    }

    @Test
    void indexDeletedItem() throws Exception {
        CamSource c = cs.findAll().get(0);
        SearchParameters sp = SearchParameters.fromQuery(c.getId()).withPrecision(20);
        assertEquals(fts.count(sp), 1);
        cs.deleteById(c.getId());
        assertEquals(fts.count(sp), 0);
    }

    @Test
    void testResultCount() throws Exception {
        assertEquals(fts.count(), 827);
    }

    @Test
    void testURL() throws Exception {
//        CamSource s = cs.findById("c621");
//        SearchResult<CamSource> k = fts.search(
//                "https://video.dot.state.mn.us/video/image/metro/C621")
//                .getResultList().get(0);
//
//        CamSource x = k.getEntity();
//        assertEquals(s, k.getEntity());

//        Object q1 = fts.search("https://video.dot.state.mn.us/video/image/metro/C621");
//        Object q = fts.search("video/image/metro/C621");
        Object q = fts.search("\"I-35W: SB @ 42nd\"");

        System.out.println();
    }

    @Test
    void testPagedSearch() throws Exception {
        int pageSize = 100;
        int count = fts.count("*");
        int expectPages = (int) Math.ceil((double) count / (double) pageSize);

        int total = 0;
        for (int i = 0; i <= expectPages; i++) {
            int resultsSize = fts.count(new SearchParameters().withPageable(PageRequest.of(i, pageSize)));
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
