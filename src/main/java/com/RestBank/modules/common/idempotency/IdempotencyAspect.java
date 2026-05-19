package com.RestBank.modules.common.idempotency;

import com.RestBank.modules.common.response.WebResponseBuilder;
import com.RestBank.modules.common.util.RequestLocalUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.HexFormat;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Around("@annotation(Idempotent)")
    public Object beforeMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object[] args = proceedingJoinPoint.getArgs();
        if(args == null || args.length == 0){
            return proceedingJoinPoint.proceed();
        }

        String requestHash = hashOfRequest(args[0]);

        if(!isNewRequest(requestHash)){
            String cached = redisTemplate.opsForValue().get(requestHash + "-result");

            if (cached != null) {
                Object prevResponseObj = objectMapper.readValue(cached, Object.class);
                log.info("duplicate request, returning original response");
                return ResponseEntity.ok(prevResponseObj); //restricts to ok 200 todo fix for 201, 3xx etc
            }
            return WebResponseBuilder.buildResponse("Duplicate action, your original request does not produce a response or may have encountered an error, kindly confirm the effects before re-attempting", true, null, HttpStatus.CONFLICT);

        }

        Object result = proceedingJoinPoint.proceed();
        if (result instanceof ResponseEntity<?> responseEntity) {

            String body = objectMapper.writeValueAsString(responseEntity.getBody());

            redisTemplate.opsForValue().set(
                    requestHash + "-result",
                    body,
                    Duration.ofMinutes(4)
            );
        }

        return result;
    }

    private String hashOfRequest(Object request){
        try{
            String json = objectMapper.writeValueAsString(request);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(json.getBytes(StandardCharsets.UTF_8));

            return RequestLocalUtil.getLoggedInSubject() + HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            log.info("error during idempotency hashing: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "system error, kindly reach out to support");
        }
    }


    private boolean isNewRequest(String requestHash) {

        try {

            long nowInMinutes = System.currentTimeMillis() / 60_000; // minutes

            List<String> keys = new LinkedList<>();

            for (int i = 0; i < 3; i++) {
                keys.add(requestHash + ":" + (nowInMinutes - i));
            }

            long count = 0;

            for(String key : keys){

                Boolean pastRequestExists = redisTemplate.hasKey(key);
                if (pastRequestExists){
                    count += 1;
                }

            }

            if (count >= 1) {
                return false;
            }

            Boolean noPresentRequestExists = redisTemplate.opsForValue()
                    .setIfAbsent(requestHash + ":" + nowInMinutes, "A REQUEST WAS HERE", Duration.ofMinutes(4));

            if(Boolean.FALSE.equals(noPresentRequestExists)){
                return false;
            }

            return true;
        } catch (Exception e) {
            log.info("unable to validate request non-redundancy with cause: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "system error, kindly reach out to support");
        }
    }
}
