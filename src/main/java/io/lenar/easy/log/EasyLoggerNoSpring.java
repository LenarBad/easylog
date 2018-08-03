package io.lenar.easy.log;

import io.lenar.easy.log.annotations.LogIt;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

import static io.lenar.easy.log.support.PJPSupport.hasMethodLevelLogItAnnotation;

public class EasyLoggerNoSpring extends UneasyLogger {

    public static final String CLASS_LEVEL_LOGIT_POINTCUT = "execution(* *(..)) && @within(annotation)";
    public static final String METHOD_LEVEL_LOGIT_POINTCUT = "execution(* *(..)) && @annotation(annotation)";

    public Object logIt(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        return logMethod(jp, annotation);
    }

    public Object logItClassLevel(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        if (hasMethodLevelLogItAnnotation(jp)) {
            return jp.proceed(jp.getArgs());
        }
        return logMethod(jp, annotation);
    }

    public Object logItMethodLevel(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        return logMethod(jp, annotation);
    }

}
