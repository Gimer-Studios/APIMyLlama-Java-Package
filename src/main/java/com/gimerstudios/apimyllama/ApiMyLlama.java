package com.gimerstudios.apimyllama;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ApiMyLlama {
    private final String ip;
    private final int port;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiMyLlama(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String generate(String apiKey, String prompt, String model, boolean stream) throws IOException, InterruptedException {
        String url = String.format("http://%s:%d/generate", ip, port);
        Map<String, Object> payload = new HashMap<>();
        payload.put("apikey", apiKey);
        payload.put("prompt", prompt);
        payload.put("model", model);
        payload.put("stream", stream);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.out.println("Error: " + response.statusCode() + " - " + response.body());
        }
        return response.body();
    }

    public Map<String, Object> getHealth(String apiKey) throws IOException, InterruptedException {
        String url = String.format("http://%s:%d/health?apikey=%s", ip, port, apiKey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), Map.class);
    }
}
