package com.example.backend.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.time.Duration;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.springframework.data.elasticsearch.repository")
public class ElasticConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(host + ":9200")
                .usingSsl(false)
                .withConnectTimeout(Duration.ofSeconds(30))
                .withSocketTimeout(Duration.ofSeconds(60))
                .withBasicAuth(username, password)
                .build();
    }
}
