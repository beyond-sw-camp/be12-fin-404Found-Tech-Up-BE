package com.example.backend.es.repository;

import com.example.backend.es.model.EsEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EsRepositoryImpl implements EsRepository {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${elasticsearch.host:http://192.0.40.207:9200}")
    private String elasticsearchHost;

    public EsRepositoryImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<EsEntity> findEntityById(String idx, String type) {
        String url;
        if (type.equals("user")){
            url = String.format("%s/user-based/_doc/%s?pretty", "http://192.0.40.207:9200", idx);
        } else {
            url = String.format("%s/item-based/_doc/%s?pretty", "http://192.0.40.207:9200", idx);
        }

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());

                // ES에서 데이터가 있는지 확인
                if (jsonNode.has("found") && jsonNode.get("found").asBoolean()) {
//                    JsonNode source = jsonNode.get("_source");
                    return Optional.of(convertToEntity(jsonNode));
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Elasticsearch 사용자 조회 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public List<EsEntity> findSimilarEntities(List<Float> vector, int resultNum, String type) {
        String url;
        if (type.equals("user")){
            url = String.format("%s/user-based/_search?pretty", "http://192.0.40.207:9200");
        } else {
            url = String.format("%s/item-based/_search?pretty", "http://192.0.40.207:9200");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            // 요청 본문 생성
            ObjectNode requestBody = objectMapper.createObjectNode();
            ObjectNode knnNode = requestBody.putObject("knn");

            knnNode.put("field", "vector");
            ArrayNode vectorArray = knnNode.putArray("query_vector");
            vector.forEach(vectorArray::add);

            knnNode.put("k", resultNum);
            knnNode.put("num_candidates", 100);

            ArrayNode sourceArray = requestBody.putArray("_source");
            sourceArray.add("vector");
//            sourceArray.add("last_updated");

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            List<EsEntity> entities = new ArrayList<>();

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode hits = jsonNode.path("hits").path("hits");

                boolean isFirst = true;
                for (JsonNode hit : hits) {
//                    JsonNode source = hit.get("_source");
                    if (isFirst) {
                        isFirst = false; // 첫 번째 항목은 건너뜀
                        continue;
                    }
                    entities.add(convertToEntity(hit));
                }
            }

            return entities;
        } catch (Exception e) {
            throw new RuntimeException("Elasticsearch 유사 사용자 검색 실패: " + e.getMessage(), e);
        }
    }

    private EsEntity convertToEntity(JsonNode jsonNode) {
        EsEntity entity = new EsEntity();
        JsonNode source = jsonNode.get("_source");
        entity.setIdx(jsonNode.path("_id").asText());

//        if (source.has("last_updated")) {
//            String dateStr = source.path("last_updated").asText();
//            // ISO 포맷 또는 적절한 포맷으로 변환
//            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
//            entity.setLastUpdated(LocalDateTime.parse(dateStr, formatter));
//        }

        if (source.has("vector") && source.get("vector").isArray()) {
            List<Float> vectorList = new ArrayList<>();
            JsonNode vectorNode = source.get("vector");
            for (JsonNode element : vectorNode) {
                vectorList.add((float) element.asDouble());
            }
            entity.setVector(vectorList);
        }

        return entity;
    }
}
