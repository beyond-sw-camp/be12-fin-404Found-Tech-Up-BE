package com.example.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class HttpClientUtil {
    private static HttpClient client;
    private static HttpRequest httpRequest;
    private static HttpResponse<String> httpResponse;

    public static String sendPost(String url, String contentType, String body) {
        try {
            client = HttpClient.newHttpClient();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return httpResponse.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getTotalAmount(String paymentId) {
        try {
            String secret = System.getenv("PORTONE_SECRET");
            client = HttpClient.newHttpClient();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.portone.io/payments/" +paymentId))
                    .header("Authorization", "PortOne " + secret)
                    .GET()
                    .build();
            httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String responseBody = httpResponse.body();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(responseBody, Map.class);
            Map<String, Object> amount = objectMapper.readValue(map.get("amount").toString(), Map.class);
            int total = Integer.parseInt(amount.get("total").toString());

            return total;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
