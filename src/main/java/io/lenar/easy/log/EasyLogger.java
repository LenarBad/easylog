package io.lenar.easy.log;


import io.lenar.easy.log.annotations.LogIt;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class EasyLogger extends UneasyLogger {

    @Pointcut("execution(* *(..))")
    public void anyMethod() {}

    @Around("anyMethod() && @within(annotation)")
    public Object logItClassLevel(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        return logMethod(jp, annotation);
    }

    @Around("anyMethod() && @annotation(annotation)")
    public Object logItMethodLevel(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        return logMethod(jp, annotation);
    }

}
