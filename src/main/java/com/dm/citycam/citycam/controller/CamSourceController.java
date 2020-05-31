package com.dm.citycam.citycam.controller;


import com.dm.citycam.citycam.config.ApiRequest;
import com.dm.citycam.citycam.config.ListResponseHeaders;
import com.dm.citycam.citycam.data.entity.CamSource;
import com.dm.citycam.citycam.data.representation.CamSourceModel;
import com.dm.citycam.citycam.data.representation.CamSourceModelAssembler;
import com.dm.citycam.citycam.data.service.CamSourceService;
import com.dm.citycam.citycam.exception.InvalidParameterException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/cameras", produces = "application/json")
public class CamSourceController {

    @Inject
    private CamSourceService cs;

    @Inject
    CamSourceModelAssembler assembler;

    @GetMapping(value = {"", "/"})
    public Object findAll(HttpServletRequest request) throws InvalidParameterException, UnsupportedEncodingException {

        ApiRequest r = new ApiRequest(request);
        CollectionModel<CamSourceModel> cm = assembler.toCollectionModel(cs.findAll(r.getPageable()));
        return ResponseEntity
                .ok()
                .headers(ListResponseHeaders.from(r, cs.count()))
                .body(cm);
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable UUID id) throws EntityNotFoundException {
        return ResponseEntity.ok(assembler.toModel(cs.findById(id)));
    }

    @PostMapping(value = {"/"})
    public Object update(@Valid @RequestBody CamSource source) {
        return ResponseEntity.ok(assembler.toModel(cs.save(cs.updateExisting(source))));
    }

    @PatchMapping(value = {"/{id}"})
    public Object patch(
            @PathVariable("id") UUID id, @RequestBody Map<String, Object> source) throws IllegalAccessException {
        return ResponseEntity.ok(assembler.toModel(cs.patch(source, id)));
    }

    @DeleteMapping(value = {"/{id}"})
    public Object delete(@PathVariable("id") UUID id) throws EntityNotFoundException {
        cs.deleteById(id);
        return ResponseEntity.ok("Success");
    }

    //
    // Mappings for TITLE based query
    //

    @GetMapping("/title/{title}")
    public Object getByTitle(@PathVariable String title) throws EntityNotFoundException {
        return ResponseEntity.ok(assembler.toModel(cs.findByTitle(title)));
    }

    @DeleteMapping(value = {"/title/{title}"})
    public Object deleteByTitle(@PathVariable("title") String title) throws EntityNotFoundException {
        cs.deleteById(cs.findByTitle(title).getId());
        return ResponseEntity.ok("Success");
    }

    @PostMapping(value = {"/title"})
    public Object updateByTitle(@Valid @RequestBody CamSource source) {
        return ResponseEntity.ok(assembler.toModel(cs.save(cs.updateExisting(source))));
    }

    @PatchMapping(value = {"/title/{title}"})
    public Object patchByTitle(
            @PathVariable String title, @RequestBody Map<String, Object> source) throws IllegalAccessException {
        CamSource camSource = cs.findByTitle(title);
        return ResponseEntity.ok(assembler.toModel(cs.patch(source, camSource.getId())));
    }


}