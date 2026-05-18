package com.RestBank.modules.common.idempotency;

import com.RestBank.modules.common.util.RequestLocalUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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
        System.out.println("Checking idempotency before execution...");

        Object[] args = proceedingJoinPoint.getArgs();
        if(args == null || args.length == 0){
            return proceedingJoinPoint.proceed();
        }

        String requestHash = hashOfRequest(args[0]);

        if(!isNewRequest(requestHash)){
            String cached = redisTemplate.opsForValue().get(requestHash + "-result");

            if (cached != null) {
                return objectMapper.readValue(cached, Object.class);
            }
            return null;
        }

        Object result = proceedingJoinPoint.proceed();

        redisTemplate.opsForValue().set(requestHash + "-result", objectMapper.writeValueAsString(result), Duration.of(4, ChronoUnit.MINUTES));
        return result;
    }

    private String hashOfRequest(Object request){
        try{
            String json = objectMapper.writeValueAsString(request);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(json.getBytes(StandardCharsets.UTF_8));

            return RequestLocalUtil.getLoggedInSubject() + HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to evaluate if action is a repetition");
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
            log.error("unable to validate request non-redundancy with cause, {}", e.getMessage());
            return false;
        }
    }
}
