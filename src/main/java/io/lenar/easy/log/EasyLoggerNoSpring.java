package io.lenar.easy.log;

import io.lenar.easy.log.annotations.LogIt;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;

import static io.lenar.easy.log.ExceptionLogger.logException;
import static io.lenar.easy.log.support.PJPSupport.hasMethodLevelLogItAnnotation;

public class EasyLoggerNoSpring extends UneasyLogger {

    public Object logItClassLevel(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        if (hasMethodLevelLogItAnnotation(jp)) {
            return jp.proceed(jp.getArgs());
        }
        return logMethod(jp, annotation);
    }

    public Object logItMethodLevel(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        return logMethod(jp, annotation);
    }

    public void logExceptionClassLevel(JoinPoint jp, LogIt annotation, Throwable e) {
        if (!hasMethodLevelLogItAnnotation(jp)) {
            logException(jp, annotation, e);
        }
    }

    public void logExceptionMethodLevel(JoinPoint jp, LogIt annotation, Throwable e) {
        logException(jp, annotation, e);
    }

}
