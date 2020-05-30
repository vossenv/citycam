package com.dm.citycam.citycam.controller;


import com.dm.citycam.citycam.data.service.CamSourceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@CrossOrigin
@RequestMapping(value = "/cameras") //, produces = "application/json")
public class CamController {

    @Inject
    private CamSourceService cs;

    @GetMapping(value = {"", "/"})
    public Object ping() {
        return ResponseEntity.ok("alive");
    }


//    private GenModelAssembler<Endpoint> ex= new GenModelAssembler<Endpoint>(this.getClass(), (Class<GenModel<Endpoint>>) new GenModel<Endpoint>().getClass());


//    @Inject
//    private EndpointService es;

//    @Inject
//    private EndpointModelAssembler ema;

//    @GetMapping(value = {"", "/"})
//    public Object findAll() throws EntityNotFoundException {
//        //return ResponseEntity.ok(es.findAll(PageRequest.of(0,100)));
//
//        Endpoint x = es.findAll(PageRequest.of(0,100)).get(0);
//
//        EndpointModel em = ema.toModel(x);
//
//        return ResponseEntity.ok(em);
//
////                .map(actorModelAssembler::toModel)
////                .map(ResponseEntity::ok)
////                .orElse(ResponseEntity.notFound().build());
//    }

//    @GetMapping(value = {"/1"})
//    public Object searchChallenge(HttpServletRequest request)
//            throws InvalidParameterException, UnsupportedEncodingException {
//        return es.findAllPaged(new ApiRequest(request))
//                .getResponse(Endpoint.class, EndpointResource.class);
//    }
//
//    @GetMapping("/{id}")
//    public Object get(@PathVariable String id) throws EntityNotFoundException {
//        return ResponseEntity.ok(es.findById(UUID.fromString(id)));
//    }


//    @PostMapping(value = {"/update"})
//    public Object addUpdateChallenge(@Valid @RequestBody Challenge challenge) throws Exception {
//        return ResponseEntity.ok(new ChallengeResource(challengeService.update(challenge)));
//    }
//
//    @DeleteMapping(value = {"/{id}"})
//    public Object deleteChallenge(@PathVariable("id") UUID id) throws EntityNotFoundException, DeleteFailedException {
//        challengeService.deleteById(id);
//        return ResponseEntity.ok("Success");
//    }
//
//    @GetMapping(value = {"/search"})
//    public Object searchChallenge(HttpServletRequest request)
//            throws InvalidParameterException, SearchFailedException, UnsupportedEncodingException, EntityNotFoundException {
//        return  challengeService.search(new SearchRequest(request))
//                .getResponse(Challenge.class, ChallengeResource.class);
//
//    }

}