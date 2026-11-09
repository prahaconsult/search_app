package cz.inizio.search_app.service;

import cz.inizio.search_app.dto.SearchResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class SerpApiService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String baseUrl;
    private final String defaultCountry;

    public SerpApiService(@Value("${serpapi.api.key}") String apiKey,
                          @Value("${serpapi.api.base-url}") String baseUrl,
                          @Value("${serpapi.default.country}") String defaultCountry) {
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.defaultCountry = defaultCountry;
    }

    public String searchRaw(String query) {
        // Api key present?
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("SerpAPI key missing in configuration");
        }

        // Parameters for SerpAPI
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("engine", "google")
                .queryParam("q", query)
                .queryParam("api_key", apiKey)
                .queryParam("gl", defaultCountry)
                .toUriString();

        // Returning result of call
        return restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);
    }

    public List<SearchResult> search(String query) {

        String rawJson = searchRaw(query);

        try {
            // Read json
            JsonNode root = objectMapper.readTree(rawJson);

            if (root.has("error")) {
                throw new RuntimeException("SerpAPI returned an error: " + root.get("error").asText());
            }

            // Only organic
            JsonNode organic = root.get("organic_results");

            //if nothing found
            if (organic == null || !organic.isArray()){
                System.out.println("--- ATTENTION: No organic results. Raw JSON: ---");
                System.out.println(rawJson);
                return List.of();
            }

            // results
            List<SearchResult> results = new ArrayList<>();
            for(JsonNode item : organic) {
                results.add(new SearchResult(
                        item.path("title").asText(""),
                        item.path("link").asText(""),
                        item.path("snippet").asText("")
                ));
            }
            return results;
        } catch(Exception e){
            throw new RuntimeException("Couldn't process JSON from SerpAPI", e);
        }
    }

    public List<SearchResult> parseJson(String rawJson) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            if (root.has("error")) {
                throw new RuntimeException("SerpAPI error: " + root.get("error").asText());
            }

            JsonNode organic = root.get("organic_results");
            if (organic == null || !organic.isArray()){
                return List.of();
            }

            List<SearchResult> results = new ArrayList<>();
            for(JsonNode item : organic) {
                results.add(new SearchResult(
                        item.path("title").asText(""),
                        item.path("link").asText(""),
                        item.path("snippet").asText("")
                ));
            }
            return results;
        } catch(Exception e){
            throw new RuntimeException("Couldn't process JSON from SerpAPI", e);
        }
    }
}
