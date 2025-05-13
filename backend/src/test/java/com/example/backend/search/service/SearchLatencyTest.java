package com.example.backend.search.service;

import com.example.backend.search.model.ProductIndexDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


@SpringBootTest
@ActiveProfiles("test")
public class SearchLatencyTest {

    @Autowired
    private ProductIndexDocument productIndexDocument;



    private final String BASE_URL = "https://localhost:8443/product/list?page=0&size=10"; // HTTPS 주의
    private final String ELASTICSEARCH_URL = "https://localhost:8443/search?keyword=AMD&category=CPU&priceLow=10000&priceHigh=100000&page=0&size=10";

    @Test
    public void testRequestTimeToProductList() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        String jsonBody = """
                            {
                              "category": "CPU",
                              "nameKeyword": "AMD",
                              "minPrice": 10000,
                              "maxPrice": 100000
                            }
                            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.ofString(jsonBody)) // 핵심!
                .build();

        // 시간 측정 시작
        long startTime = System.currentTimeMillis();

        // HTTPS 요청 (SSL 검증이 필요 없는 설정은 테스트 설정에서 별도로 구성 필요)
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 시간 측정 종료
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        System.out.println("RDB 검색 HTTP 요청 소요 시간: " + duration + "ms");
        System.out.println("응답 코드: " + response.statusCode());
        System.out.println("응답 본문: " + response.body());

        // 여긴 ElasticSeach
        startTime = System.currentTimeMillis();
        ResponseEntity<String> response2 = restTemplate.getForEntity(ELASTICSEARCH_URL, String.class);
        endTime = System.currentTimeMillis();
        duration = endTime - startTime;
        System.out.println("엘라스틱서치 HTTP 요청 소요 시간: " + duration + "ms");
        System.out.println("응답 코드: " + response2.getStatusCode());
        System.out.println("응답 본문: " + response2.getBody());
    }
}
