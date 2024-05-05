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

    @Pointcut("within(com.example.bank.service.*))")
    public void anyMethodFromServices(){};

    @Pointcut("@annotation(com.example.bank.logging.ManualLogging)")
    public void anyMarkedMethodManualLogging(){};

    @Pointcut("execution(public * *(*,..))")
    public void anyPublicMethodWithArgs(){};

    @Pointcut("execution(public * *())")
    public void anyPublicMethodWithoutArgs(){};



    @Around("anyMethodFromServices()&&anyPublicMethodWithArgs()&&!anyMarkedMethodManualLogging()")
    public Object aroundAnyServiceMethodsWithArgs(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.trace("Execute {} with args={}",proceedingJoinPoint.getSignature(),Arrays.toString(proceedingJoinPoint.getArgs()));
        var targetMethodResult=proceedingJoinPoint.proceed();
        if (targetMethodResult!=null)log.trace("Return {}",targetMethodResult.getClass());
        else log.trace("Method {} was executed",proceedingJoinPoint.getSignature());
        return targetMethodResult;
    }

    @Around("anyMethodFromServices()&&anyPublicMethodWithoutArgs()&&!anyMarkedMethodManualLogging()")
    public Object aroundAnyServiceMethodsWithoutArgs(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.trace("Execute {} ",proceedingJoinPoint.getSignature());
        var targetMethodResult=proceedingJoinPoint.proceed();
        if (targetMethodResult!=null) log.trace("Return {}",targetMethodResult.getClass());
        else log.trace("Method {} was executed",proceedingJoinPoint.getSignature());
        return targetMethodResult;
    }


}
