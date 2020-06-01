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
        camSourceModels.add(new Link(request.getLastCall()).withSelfRel());
        camSourceModels.add(new Link(request.getNextURL()).withRel("next"));
        camSourceModels.add(new Link(request.getPrevURL()).withRel("previous"));
        camSourceModels.add(new Link(request.getFirstURL()).withRel("first"));
        camSourceModels.add(new Link(request.getLastURL()).withRel("last"));
        return camSourceModels;
    }
}