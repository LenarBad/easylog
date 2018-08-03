package io.lenar.easy.log;


import io.lenar.easy.log.annotations.LogIt;

import io.lenar.easy.log.support.PJPSupport;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import static io.lenar.easy.log.support.PJPSupport.hasMethodLevelLogItAnnotation;

@Aspect
public class EasyLogger extends UneasyLogger {

    @Pointcut("execution(* *(..))")
    public void anyMethod() {}

    @Around("anyMethod() && @within(annotation)")
    public Object logItClassLevel(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        if (hasMethodLevelLogItAnnotation(jp)) {
            return jp.proceed(jp.getArgs());
        }
        return logMethod(jp, annotation);
    }

    @Around("anyMethod() && @annotation(annotation)")
    public Object logItMethodLevel(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        return logMethod(jp, annotation);
    }

}
