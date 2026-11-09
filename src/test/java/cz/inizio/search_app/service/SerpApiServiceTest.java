package cz.inizio.search_app.service;

import cz.inizio.search_app.dto.SearchResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerpApiServiceTest {

    @Test
    void testParsovaniJsonu() {
        SerpApiService service = new SerpApiService("fake-key", "fake-url", "cz");

        String fakeJson = """
            {
              "organic_results": [
                {
                  "title": "First result",
                  "link": "https://example.com/1",
                  "snippet": "Text 1"
                },
                {
                  "title": "Second result",
                  "link": "https://example.com/2",
                  "snippet": "Text 2"
                }
              ]
            }
            """;

        List<SearchResult> results = service.parseJson(fakeJson);

        assertEquals(2, results.size());
        assertEquals("First result", results.get(0).title());
        assertEquals("https://example.com/2", results.get(1).link());
    }

    @Test
        void testEmptyJson() {
            SerpApiService service = new SerpApiService("fake-key", "fake-url", "cz");

        String emptyJson = "{}";
        List<SearchResult> results = service.parseJson(emptyJson);
        assertEquals(0, results.size());
    }
}