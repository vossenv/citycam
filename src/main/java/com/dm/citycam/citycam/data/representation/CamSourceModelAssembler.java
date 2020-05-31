package com.dm.citycam.citycam.data.representation;

import com.dm.citycam.citycam.config.RequestInfo;
import com.dm.citycam.citycam.controller.CamSourceController;
import com.dm.citycam.citycam.data.entity.CamSource;
import lombok.SneakyThrows;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CamSourceModelAssembler extends RepresentationModelAssemblerSupport<CamSource, CamSourceModel> {

    public CamSourceModelAssembler() {
        super(CamSourceController.class, CamSourceModel.class);
    }

    @SneakyThrows
    @Override
    public CamSourceModel toModel(CamSource entity) {
        CamSourceModel camSourceModel = new CamSourceModel(entity);
        camSourceModel.add(linkTo(methodOn(CamSourceController.class).get(entity.getId())).withSelfRel());
        camSourceModel.add(linkTo(methodOn(CamSourceController.class).findAll(null)).withRel("list"));
        camSourceModel.add(linkTo(methodOn(CamSourceController.class).deleteGet(entity.getId())).withRel("delete"));
        return camSourceModel;
    }

    @SneakyThrows
    public CollectionModel<CamSourceModel> toCollectionModel(Iterable<? extends CamSource> entities, RequestInfo request) {
        CollectionModel<CamSourceModel> camSourceModels = super.toCollectionModel(entities);
        camSourceModels.add(Link.of(request.getLastCall()).withSelfRel());
        camSourceModels.add(Link.of(request.getNextURL()).withRel("next"));
        camSourceModels.add(Link.of(request.getPrevURL()).withRel("previous"));
        camSourceModels.add(Link.of(request.getFirstURL()).withRel("first"));
        camSourceModels.add(Link.of(request.getLastURL()).withRel("last"));
        return camSourceModels;
    }
}