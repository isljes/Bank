package com.example.bank.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(com.example.bank.services.*))")
    public void anyMethodFromServices(){};

    @Pointcut("execution(public * *(*,..))")
    public void anyPublicServiceMethodWithArgs(){};

    @Pointcut("execution(public * *())")
    public void anyPublicServiceMethodWithoutArgs(){};



    @Around("anyMethodFromServices()&&anyPublicServiceMethodWithArgs()")
    public Object aroundAnyServiceMethodsWithArgs(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.trace("Execute {} with arg={}",proceedingJoinPoint.getSignature(),Arrays.toString(proceedingJoinPoint.getArgs()));
        var targetMethodResult=proceedingJoinPoint.proceed();
        log.trace("Return {}",targetMethodResult.getClass());
        return targetMethodResult;
    }
    @Around("anyMethodFromServices()&&anyPublicServiceMethodWithoutArgs()")
    public Object aroundAnyServiceMethodsWithoutArgs(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.trace("Execute {} ",proceedingJoinPoint.getSignature());
        var targetMethodResult=proceedingJoinPoint.proceed();
        log.trace("Return {}",targetMethodResult.getClass());
        return targetMethodResult;
    }

}
