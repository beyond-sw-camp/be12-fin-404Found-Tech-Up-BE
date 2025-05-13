package com.example.backend.es.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsEntity {
    private String idx;
    private List<Float> vector;
//    private LocalDateTime lastUpdated;
}
