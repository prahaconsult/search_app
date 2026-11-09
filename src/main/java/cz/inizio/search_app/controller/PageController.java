package cz.inizio.search_app.controller;

import cz.inizio.search_app.service.SerpApiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final SerpApiService searchService;

    public PageController(SerpApiService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String q, Model model) {

        if (q != null && !q.isBlank()) {
            model.addAttribute("q", q);
            model.addAttribute("results", searchService.search(q));
        }
        return "index";
    }
}