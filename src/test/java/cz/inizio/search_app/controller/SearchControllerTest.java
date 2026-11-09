package cz.inizio.search_app.controller;

import cz.inizio.search_app.dto.SearchResult;
import cz.inizio.search_app.service.SerpApiService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchControllerTest {

    @Test
    void testExportCsv() {

        SerpApiService fakeService = mock(SerpApiService.class);

        when(fakeService.search("test")).thenReturn(List.of(
                new SearchResult("Muj nadpis", "https://example.com", "Muj popisek")
        ));

        SearchController controller = new SearchController(fakeService);

        ResponseEntity<String> response = controller.export("test", "csv");

        assertEquals(200, response.getStatusCode().value());

        String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertTrue(contentDisposition != null && contentDisposition.contains("attachment"));

        assertTrue(response.getBody().contains("Muj nadpis"));
    }
}