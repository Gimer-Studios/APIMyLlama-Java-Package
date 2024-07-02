package com.gimerstudios.apimyllama;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;

public class ApiMyLlama {

    private final String serverIp;
    private final int serverPort;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public ApiMyLlama(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }

    public String generate(String apikey, String prompt, String model, boolean stream, Map<String, String> images, boolean raw) throws IOException, InterruptedException {
        String url = String.format("http://%s:%d/generate", serverIp, serverPort);
        Map<String, Object> payload = Map.of(
                "apikey", apikey,
                "prompt", prompt,
                "model", model,
                "stream", stream,
                "images", images,
                "raw", raw
        );
        String payloadJson = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(payloadJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        return response.body();
    }

    public String getHealth(String apikey) throws IOException, InterruptedException {
        String url = String.format("http://%s:%d/health?apikey=%s", serverIp, serverPort, apikey);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        return response.body();
    }
}
