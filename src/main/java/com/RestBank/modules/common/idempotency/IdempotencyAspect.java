package com.assessment.api_design.common.idempotency;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {
    private final HashValidator hashValidator;

    @Around("@annotation(Idempotent)")
    public Object beforeMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("Checking idempotency before execution...");

        Object[] args = proceedingJoinPoint.getArgs();
        if(args == null || args.length == 0){
            return proceedingJoinPoint.proceed();
        }

        if(!hashValidator.isNewRequest(args[0])){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
            return "Request has already been received";
        }

        return proceedingJoinPoint.proceed();
    }
}
