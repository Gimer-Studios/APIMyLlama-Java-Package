package com.gimerstudios.apimyllama;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiMyLlama {
    private final String ip;
    private final int port;
    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public ApiMyLlama(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> generate(String apikey, String prompt, String model, boolean stream, Object[] images, boolean raw) throws Exception {
        String url = String.format("http://%s:%d/generate", ip, port);
        Map<String, Object> payload = Map.of(
                "apikey", apikey,
                "prompt", prompt,
                "model", model,
                "stream", stream,
                "images", images,
                "raw", raw
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .POST(BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Request failed with status code: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), Map.class);
    }

    public Map<String, Object> getHealth(String apikey) throws Exception {
        String url = String.format("http://%s:%d/health?apikey=%s", ip, port, apikey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Request failed with status code: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), Map.class);
    }
}
