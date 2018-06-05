package io.lenar.easy.log;


import io.lenar.easy.log.annotations.LogCall;
import io.lenar.easy.log.annotations.LogCalls;
import io.lenar.easy.log.annotations.LogMethod;
import io.lenar.easy.log.annotations.LogMethods;
import io.lenar.easy.log.support.JoinPointLogger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class EasyLogger extends JoinPointLogger {

    @Pointcut("execution(* *(..))")
    public void anyMethod() {}

    @Around("anyMethod() && @annotation(annotation)")
    public Object logCall(ProceedingJoinPoint jp, LogCall annotation) throws Throwable {
        return logAsCall(jp, annotation.name(), annotation.level());
    }

    @Around("anyMethod() && @within(annotation)")
    public Object logCalls(ProceedingJoinPoint jp, LogCalls annotation) throws Throwable {
        return logAsCall(jp, annotation.name(), annotation.level());
    }

    @Around("anyMethod() && @annotation(annotation)")
    public Object logMethod(ProceedingJoinPoint jp, LogMethod annotation) throws Throwable {
        return logAsMethod(jp, annotation.level());
    }

    @Around("anyMethod() && @within(annotation)")
    public Object logMethods(ProceedingJoinPoint jp, LogMethods annotation) throws Throwable {
        return logAsMethod(jp, annotation.level());
    }


}
