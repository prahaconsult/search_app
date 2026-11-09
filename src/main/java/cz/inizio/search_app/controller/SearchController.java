package cz.inizio.search_app.controller;

import cz.inizio.search_app.dto.SearchResult;
import cz.inizio.search_app.service.SerpApiService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SerpApiService serpApiService;

    public SearchController(SerpApiService serpApiService) {
        this.serpApiService = serpApiService;
    }

    @GetMapping("/raw")
    public String searchRaw(@RequestParam String q){
        return serpApiService.searchRaw(q);
    }

    @GetMapping
    public List<SearchResult> search(@RequestParam String q) {
        return serpApiService.search(q);
    }

    @GetMapping("/export")
    public ResponseEntity<String> export(@RequestParam String q, @RequestParam(defaultValue = "json") String format) {
        List<SearchResult> results = serpApiService.search(q);

        String content;
        String filename;
        MediaType mediaType;

        if ("csv".equalsIgnoreCase(format)) {
            content = convertToCsv(results);
            filename = "results.csv";
            mediaType = MediaType.TEXT_PLAIN;
        } else {
            content = convertToJson(results);
            filename = "results.json";
            mediaType = MediaType.APPLICATION_JSON;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(content);
    }

    private String convertToCsv(List<SearchResult> results) {
        StringBuilder sb = new StringBuilder("title,link,snippet\n");
        for (SearchResult r : results) {
            sb.append("\"").append(r.title().replace("\"", "\"\"")).append("\",")
                    .append("\"").append(r.link().replace("\"", "\"\"")).append("\",")
                    .append("\"").append(r.snippet().replace("\"", "\"\"")).append("\"\n");
        }
        return sb.toString();
    }

    private String convertToJson(List<SearchResult> results) {
        try {
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(results);
        } catch (Exception e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }
}
