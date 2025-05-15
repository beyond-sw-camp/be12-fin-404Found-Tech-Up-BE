package com.example.backend.util;

import com.example.backend.search.model.ProductIndexDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    public static boolean requestRefund(String paymentId) {
        String secret = System.getenv("PORTONE_SECRET");
        String url    = "https://api.portone.io/payments/" + paymentId + "/cancel";

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "PortOne " + secret)
                    .POST(HttpRequest.BodyPublishers.ofString("{\"reason\": \"User Request\"}"))
                    .build();
            HttpResponse<String> resp = HttpClient.newHttpClient()
                    .send(req, HttpResponse.BodyHandlers.ofString());

            // 200번대 응답이면 OK
            return resp.statusCode() >= 200 && resp.statusCode() < 300;
        } catch (Exception e) {
            throw new RuntimeException("Refund request failed", e);
        }
    }

    public static List<ProductIndexDocument> getSearchResults(String elasticHost_static, String category, String searchKeyword, Integer page, Integer size) throws IOException, InterruptedException {
            String body = "";
            if (category == null || category.isBlank()) {
                body = """
                    {
                      "from": """+ page + """
                      ,
                      "size": """+ size +"""
                      ,
                      "query": {
                        "bool": {
                          "must": [
                            {
                              "match_phrase": {
                                "productname": \"""" + searchKeyword + """
                              \"
                              }
                            }
                          ]
                        }
                      }
                    }
                    """;
            } else {
                body = """
                    {
                      "from": """+ page + """
                      ,
                      "size": """+ size +"""
                      ,
                      "query": {
                        "bool": {
                          "must": [
                            {
                              "match_phrase": {
                                "productname": \"""" + searchKeyword + """
                              \"
                              }
                            },
                            {
                              "match_phrase": {
                                "category": \"""" + category + """
                              \"
                              }
                            }
                          ]
                        }
                      }
                    }
                    """;
            }

            HttpRequest request = HttpRequest.newBuilder().timeout(Duration.ofSeconds(30))
                .uri(URI.create("http://"+ elasticHost_static + ":9200/product/_search" ))
                .method("GET", HttpRequest.BodyPublishers.ofString(body))
                .setHeader("Content-Type", "application/json")
                .build();
            HttpResponse<String> resp = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String responsebody = resp.body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responsebody);
            List<ProductIndexDocument> result = new ArrayList<>();
            if (root.has("hits") && root.get("hits").has("hits")) {
                Iterator<JsonNode> values = root.get("hits").withArrayProperty("hits").elements();
                while (values.hasNext()) {
                    JsonNode value = values.next().get("_source");
                    String text = value.toPrettyString();
                    result.add(new ObjectMapper().readValue(text, ProductIndexDocument.class));
                }
            }
            return result;
    }
}
