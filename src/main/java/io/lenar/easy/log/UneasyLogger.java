package io.lenar.easy.log;

import static io.lenar.easy.log.support.PJPSupport.getMethodParameters;
import static io.lenar.easy.log.support.PJPSupport.getMethodSignatureAsString;
import static io.lenar.easy.log.support.PJPSupport.isVoid;
import static io.lenar.easy.log.support.SerializationSupport.objectToString;
import static io.lenar.easy.log.support.SerializationSupport.paramsToString;

import java.util.Map;

import io.lenar.easy.log.annotations.LogIt;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.LoggerFactory;

public class UneasyLogger {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger("UneasyLogger");

    protected Object logMethod(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        try {
            logMethodInvocation(
                    getMethodSignatureAsString(jp, true, annotation.ignoreParameters(), annotation.maskFields()),
                    getMethodParameters(jp, annotation.ignoreParameters()),
                    annotation);
        } catch (Exception ex) {
            log("Failed to process and log method's parameters \n" +
                    getMethodSignatureAsString(jp, true, annotation.ignoreParameters(), annotation.maskFields()),
                    annotation.level());
        }

        long startTime = System.currentTimeMillis();
        Object result = jp.proceed(jp.getArgs());
        long endTime = System.currentTimeMillis();

        try {
            logMethodReturn(
                    endTime - startTime,
                    getMethodSignatureAsString(jp, false, annotation.ignoreParameters(), annotation.maskFields()),
                    isVoid(jp),
                    result,
                    annotation);
        } catch (Exception ex) {
            log("Failed to process and log method's return \n" +
                            getMethodSignatureAsString(jp, false, annotation.ignoreParameters(), annotation.maskFields()),
                    annotation.level());
        }

        return result;
    }

    private void logMethodInvocation(String methodName, Map<String, Object> params, LogIt annotation) {
        String message = "\n-> " + methodName + "\n";
        if (!annotation.label().isEmpty()) message = "\n" + annotation.label() + message;
        if (!params.isEmpty()) message =
                message + paramsToString(params, annotation.maskFields(), annotation.prettyPrint(), annotation.logNulls()) + "\n";
        log(message, annotation.level());
    }

    private void logMethodReturn(long executionTime, String methodName, boolean isVoid, Object result, LogIt annotation) {
        String message = "\nExecution/Response time:  " + executionTime + "ms\n";
        if (!annotation.label().isEmpty()) message = message + annotation.label() + "\n";
        message = message + "<- " + methodName + "\n";
        if (!isVoid) message =
                message + objectToString(result, annotation.maskFields(), annotation.prettyPrint(), annotation.logNulls()) + "\n";
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
