package io.lenar.easy.log;

import io.lenar.easy.log.annotations.LogIt;

import org.aspectj.lang.ProceedingJoinPoint;

public class EasyLoggerNoSpring extends UneasyLogger {

    public static final String CLASS_LEVEL_LOGIT_POINTCUT = "execution(* *(..)) && @within(annotation)";
    public static final String METHOD_LEVEL_LOGIT_POINTCUT = "execution(* *(..)) && @annotation(annotation)";

    public Object logIt(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        return logMethod(jp, annotation);
    }

}
