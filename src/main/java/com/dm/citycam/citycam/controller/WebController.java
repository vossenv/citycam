package com.dm.citycam.citycam.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = {"", "/"}, produces = "application/json")
public class WebController {
    @GetMapping(value = {"", "/"})
    public Object ping() {

        return ResponseEntity.ok("alive");
    }

}