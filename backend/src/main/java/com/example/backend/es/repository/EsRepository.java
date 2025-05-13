package com.example.backend.es.repository;

import com.example.backend.es.model.EsEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EsRepository {
    /**
     * 특정 사용자 조회
     * @param idx 사용자 인덱스
     * @return 사용자 정보
     */
    Optional<EsEntity> findEntityById(String idx, String type);

    /**
     * 벡터 유사성 검색으로 관련 사용자 찾기
     * @param vector 검색할 벡터
     * @param resultNum 결과 개수
     * @return 유사한 사용자 목록
     */
    List<EsEntity> findSimilarEntities(List<Float> vector, int resultNum, String type);
}
