package com.example.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class RedisLuaConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedisLuaConfig.class);
    @Bean
    public DefaultRedisScript<List> issueCouponLuaScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        loadLuaFromResource("/lua/coupon_issue.lua");
        script.setLocation(new ClassPathResource("/lua/coupon_issue.lua"));
        script.setResultType(List.class);
        return script;
    }

    // 클래스 내부


    // 스크립트 로드 메서드 예시
    public String loadLuaFromResource(String path) {
        InputStream in = this.getClass().getResourceAsStream(path);
        if (in == null) {
            logger.error("Lua 스크립트 로드 실패: 리소스를 찾을 수 없습니다. path={}", path);
            return "";
        } else {
            try {
                int available = in.available();
                logger.info("Lua 스크립트 로드 성공: path={} ({} bytes)", path, available);
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                logger.error("Lua 스크립트 읽기 실패: path={}", path, e);
                return "";
            } finally {
                try { in.close(); } catch (IOException ignored) {}
            }
        }
    }


}
