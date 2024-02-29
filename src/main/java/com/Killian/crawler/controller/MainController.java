package com.Killian.crawler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class MainController {
    
    @GetMapping("")
    public String homePage() {
        return "index";
    }

    @GetMapping("/clonepage") 
    public String clonePage() {
        return "clonepage";
    }
}
