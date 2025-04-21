package com.example.backend.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClientUtil {
    private static HttpClient client;
    private static HttpResponse<String> httpResponse;

    public static double getTotalAmount(String paymentId) {
        try {
            String secret = System.getenv("PORTONE_SECRET");
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.portone.io/payments/"
                            + URLEncoder.encode(paymentId, StandardCharsets.UTF_8)))
                    .header("Authorization", "PortOne " + secret)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            // parse with Jackson tree for clarity
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root   = mapper.readTree(body);

            JsonNode amountNode;
            if (root.has("data") && root.path("data").has("amount")) {
                amountNode = root.path("data").path("amount");
            } else {
                amountNode = root.path("amount");
            }

            return amountNode.path("total").asDouble();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("PortOne 조회 실패", e);
        }
    }

}
