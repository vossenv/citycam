package com.dm.citycam.citycam.controller;


import com.dm.citycam.citycam.config.RequestInfo;
import com.dm.citycam.citycam.data.entity.CamSource;
import com.dm.citycam.citycam.data.representation.CamSourceModel;
import com.dm.citycam.citycam.data.representation.CamSourceModelAssembler;
import com.dm.citycam.citycam.data.service.CamSourceService;
import com.dm.citycam.citycam.exception.InvalidParameterException;
import com.dm.citycam.citycam.exception.SearchFailedException;
import com.dm.citycam.citycam.search.SearchParameters;
import com.dm.citycam.citycam.search.SearchResult;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping(value = "/cameras", produces = "application/json")
public class CamSourceController {

    @Inject
    private CamSourceService cs;

    @Inject
    CamSourceModelAssembler assembler;

    @GetMapping(value = {"", "/"})
    public Object findAll(HttpServletRequest request) throws InvalidParameterException {

        RequestInfo r = new RequestInfo(request);
        r.updateResults(cs.count());
        CollectionModel<CamSourceModel> cm = assembler.toCollectionModel(cs.findAll(r.getPageable()), r);
        return ResponseEntity
                .ok()
                .headers(r.getHeaders())
                .body(cm);
    }

    @GetMapping(value = {"/search"})
    public Object search(HttpServletRequest request) throws InvalidParameterException, SearchFailedException {

        RequestInfo r = new RequestInfo(request);
        SearchResult<CamSource> sr = cs.search(new SearchParameters(
                r.getQuery(),
                r.getPageable(),
                r.getFilter(),
                r.getPrecision()));

        r.updateResults(sr.getResultTotalCount(), sr.getSearchTime());
        return ResponseEntity.ok()
                .headers(r.getHeaders())
                .body(assembler.toCollectionModel(sr.getResultList()));
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable String id) throws EntityNotFoundException {
        return ResponseEntity.ok(assembler.toModel(cs.findById(id.toLowerCase())));
    }

    @PostMapping(value = {"/"})
    public Object update(@Valid @RequestBody CamSource source) {
        return ResponseEntity.ok(assembler.toModel(cs.save(source)));
    }

    @PatchMapping(value = {"/{id}"})
    public Object patch(
            @PathVariable("id") String id, @RequestBody Map<String, Object> source) throws IllegalAccessException {
        return ResponseEntity.ok(assembler.toModel(cs.patch(source, id.toLowerCase())));
    }

    @GetMapping(value = {"/{id}/delete"})
    public Object deleteGet(@PathVariable("id") String id) throws EntityNotFoundException {
        cs.deleteById(id.toLowerCase());
        return ResponseEntity.ok("Success");
    }
}