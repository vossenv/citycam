package com.dm.citycam.citycam.data.representation;

import com.dm.citycam.citycam.controller.CamSourceController;
import com.dm.citycam.citycam.data.entity.CamSource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CamSourceModelAssembler extends RepresentationModelAssemblerSupport<CamSource, CamSourceModel> {


    public CamSourceModelAssembler() {
        super(CamSourceController.class, CamSourceModel.class);
    }

    @Override
    public CamSourceModel toModel(CamSource entity) {
        CamSourceModel camSourceModel = new CamSourceModel(entity);

        camSourceModel.add(linkTo(
                methodOn(CamSourceController.class).get(entity.getId()))
                .withSelfRel());
        return camSourceModel;
    }

    @Override
    public CollectionModel<CamSourceModel> toCollectionModel(Iterable<? extends CamSource> entities) {
        CollectionModel<CamSourceModel> camSourceModels = super.toCollectionModel(entities);
        return camSourceModels;
    }
//        body.add(new Link(lastCall, "self"));
//
//        if (pageCount > 1) {
//            body.add(new Link(lastCall.replaceAll("(page=)\\d*", "page=" + next), "next"));
//            body.add(new Link(lastCall.replaceAll("(page=)\\d*", "page=" + prev), "previous"));
//        }
}