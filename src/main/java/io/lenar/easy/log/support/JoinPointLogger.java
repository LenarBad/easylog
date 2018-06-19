package io.lenar.easy.log.support;

import java.util.Map;

import io.lenar.easy.log.Level;
import io.lenar.easy.log.annotations.LogIt;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoinPointLogger extends LogSupport {

    private static Logger logger = LoggerFactory.getLogger("EasyLogger");

    protected Object logMethod(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        logMethodInvocation(
                getMethodSignatureAsString(jp, true, annotation.ignoreParameters()),
                getMethodParameters(jp, annotation.ignoreParameters()), annotation);

        long startTime = System.currentTimeMillis();
        Object result = jp.proceed(jp.getArgs());
        long endTime = System.currentTimeMillis();

        logMethodReturn(
                endTime - startTime,
                getMethodSignatureAsString(jp, false, annotation.ignoreParameters()),
                isVoid(jp), result, annotation);

        return result;
    }

    private void logMethodInvocation(String methodName, Map<String, Object> params, LogIt annotation) {
        String message = "\n-> " + methodName + "\n";
        if (!annotation.label().isEmpty()) message = "\n" + annotation.label() + message;
        if (!params.isEmpty()) message = message + objectToString(params) + "\n";
        log(message, annotation.level());
    }

    private void logMethodReturn(long executionTime, String methodName, boolean isVoid, Object result, LogIt annotation) {
        String message = "\nExecution/Response time:  " + executionTime + "ms\n";
        if (!annotation.label().isEmpty()) message = message + annotation.label() + "\n";
        message = message + "<- " + methodName + "\n";
        if (!isVoid) message = message + objectToString(result, annotation.maskFields()) + "\n";
        log(message, annotation.level());
    }


    private void log(String message, Level level) {
        switch (level) {
            case DEBUG: logger.debug(message); break;
            case INFO: logger.info(message); break;
            case WARN: logger.warn(message); break;
            case ERROR: logger.error(message);
        }
    }

}
