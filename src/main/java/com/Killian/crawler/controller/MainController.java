package com.Killian.crawler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("")
public class MainController {

    @Autowired
    private CrawlController crawlController;

    @GetMapping("")
    public String homePage() {
        return "index";
    }

    @GetMapping("/clonepage")
    public String clonePage(Model model) {
        model.addAttribute("src", crawlController.getCloneSrc());
        return "clonepage";
    }
}
