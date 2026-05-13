package com.assessment.api_design.common.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashValidator {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public boolean isNewRequest(Object request) {

        try{
            String json = objectMapper.writeValueAsString(request);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(json.getBytes(StandardCharsets.UTF_8));

            String requestHash =
                    HexFormat.of().formatHex(hashBytes);

            long now = System.currentTimeMillis() / 1000; // seconds

            List<String> keys = new ArrayList<>();

            for (int i = 0; i < 60; i++) {
                keys.add(requestHash + ":" + (now - i));
            }

            // fetch all counters in last 60 seconds
            List<String> values = redisTemplate.opsForValue().multiGet(keys);

            long total = 0;

            if (values != null) {
                for (String v : values) {
                    if (v != null) {
                        total += Long.parseLong(v);
                    }
                }
            }

            if (total >= 1) {
                return false;
            }

            // increment current bucket
            String currentKey = requestHash + ":" + now;

            redisTemplate.opsForValue().increment(currentKey);

            // expire after window + buffer
            redisTemplate.expire(currentKey, Duration.ofMinutes(2));

            return true;
        }catch (Exception e){
            log.error("unable to validate request non-redundancy with cause, {}", e.getMessage());
            return false;
        }
    }
}
