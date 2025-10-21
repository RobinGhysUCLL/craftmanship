package be.ucll.group8.craftmanshipgroep8.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class testcontroller {

    @GetMapping
    public String ok() {
        return "ok";
    }

}
