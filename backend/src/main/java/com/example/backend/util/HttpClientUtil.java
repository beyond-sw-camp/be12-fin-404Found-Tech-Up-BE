package com.example.backend.util;

import com.example.backend.search.model.ProductIndexDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    public static List<ProductIndexDocument> getSearchResults(String elasticHost_static, String category, Double priceLow, Double priceHigh, String searchKeyword, Integer page, Integer size) throws IOException, InterruptedException {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://"+ elasticHost_static + ":9200/product/_search" ))
                    .method("GET", HttpRequest.BodyPublishers.ofString("""
                            {
                              "from": """+ page + """
                              ,
                              "size": """+ size +"""
                              ,
                              "query": {
                                "bool": {
                                  "must": [
                                    {
                                      "range": {
                                        "price": {
                                            "lt": """ + priceHigh + """
                                            ,
                                            "gt": """ + priceLow + """
                                        }
                                      }
                                    },
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
                            """))
                    .setHeader("Content-Type", "application/json")
                    .build();
            HttpResponse<String> resp = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String body = resp.body();
            System.out.println(body);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            List<ProductIndexDocument> result = new ArrayList<>();
            if (root.has("hits") && root.path("hits").has("hits")) {
                List<JsonNode> values = root.path("hits").withArray("hits");
                for (JsonNode value : values) {
                    result.add(new ObjectMapper().readValue(value.toString(), ProductIndexDocument.class));
                }
            }
            return result;
    }
}
