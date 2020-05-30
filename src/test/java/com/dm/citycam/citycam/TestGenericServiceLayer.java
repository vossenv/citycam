package com.dm.citycam.citycam;


import com.dm.citycam.citycam.data.entity.CamSource;
import com.dm.citycam.citycam.data.service.CamSourceService;
import com.dm.citycam.citycam.exception.GenException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TestGenericServiceLayer {

    @Inject
    private CamSourceService cs;

    @FunctionalInterface
    interface ExceptionCheck<T> {
        void execute(T t) throws GenException;
    }

    private void MatchException(ExceptionCheck ec, Object o, Class c) {
        try {
            ec.execute(o);
        } catch (Exception e) {
            Throwable t = ExceptionUtils.getRootCause(e);
            assertEquals((null == t ? e : t).getClass(), c);
        }
    }


    @BeforeEach
    public void before() {
        CamSource c = new CamSource();
        c.setTitle("title");
        c.setUrl("https://example/com");
        cs.save(c);
    }

    @AfterEach
    public void after(){
        cs.clear();
    }

    @Test
    void TestFind() {
        ExceptionCheck<UUID> ec = (e) -> cs.findById(e);
        assertEquals(1, cs.findAll().size());
        MatchException(ec, UUID.randomUUID(), EntityNotFoundException.class);
        MatchException(ec, null, IllegalArgumentException.class);
    }

    @Test
    void TestAddSimple() {
        CamSource c = cs.findAll().get(0);
        assertNotNull(c.getId());
        assertNotNull(c.getEnabled());
        assertNotNull(c.getCreatedDate());
        assertNotNull(c.getLastModifiedDate());
    }

    @Test
    void TestUpdate() {
        CamSource c = cs.findAll().get(0);
        c.setDescription("12345");
        cs.save(c);
        assertEquals(c, cs.findById(c.getId()));
    }

    @Test
    void TestImmutableUpdate() {

        CamSource c = cs.findAll().get(0);
        LocalDateTime createdOriginal = c.getCreatedDate();

        c.setCreatedDate(LocalDateTime.MIN);
        assertEquals(cs.save(c).getCreatedDate(), createdOriginal);

        int cursize = cs.findAll().size();
        c.setId(UUID.randomUUID());
        c.setDescription("Different");
        c = cs.save(c);
        assertTrue(cs.existsById(c.getId()));
        assertEquals(cursize + 1, cs.findAll().size());
    }

    @Test
    void TestAudit() throws Exception {

        CamSource c = cs.findAll().get(0);
        c.setDescription("An update!");
        Thread.sleep(500);
        c = cs.save(c);

        CamSource cur = cs.findById(c.getId());
        assertNotEquals(cur.getLastModifiedDate(), cur.getCreatedDate());
    }

    @Test
    void TestDelete() {

        UUID id = cs.findAll().get(0).getId();
        cs.deleteById(id);
        assertFalse(cs.existsById(id));

        ExceptionCheck<UUID> ec = (e) -> cs.deleteById(e);
        MatchException(ec, id, EntityNotFoundException.class);
        MatchException(ec, UUID.randomUUID(), EntityNotFoundException.class);
        MatchException(ec, null, IllegalArgumentException.class);

    }
}
