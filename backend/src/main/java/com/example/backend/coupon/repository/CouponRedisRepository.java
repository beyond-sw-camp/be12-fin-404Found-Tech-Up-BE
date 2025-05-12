package com.example.backend.coupon.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /** Set 에 값이 있는지 */
    public Boolean sIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /** Set 크기 조회 */
    public Long sCard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /** List 오른쪽에 push */
    public Long rPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /** Set 에 추가 (신규 추가 시 true 반환) */
    public boolean sAdd(String key, String value) {
        Long added = redisTemplate.opsForSet().add(key, value);
        return added != null && added > 0;
    }

    /** Set 에서 제거 (제거 성공 시 true 반환) */
    public boolean sRem(String key, String value) {
        Long removed = redisTemplate.opsForSet().remove(key, value);
        return removed != null && removed > 0;
    }

    /** Hash 필드를 delta 만큼 증가(감소) */
    public Long hIncrBy(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /** Hash 필드 값 조회 (값 없으면 0 반환) */
    public Long hGet(String hashKey, String field) {
        Object value = redisTemplate.opsForHash().get(hashKey, field);
        if (value == null) {
            return 0L;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(
                    String.format("Redis 해시[%s] 필드[%s] 값이 숫자가 아닙니다: %s",
                            hashKey, field, value),
                    ex
            );
        }
    }

    /** 모든 키-값 초기화 */
    public void flushAll() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .flushAll();
    }
}
