package io.lenar.easy.log;

import static io.lenar.easy.log.annotations.Style.AS_IS;
import static io.lenar.easy.log.support.PJPSupport.getMethodParameters;
import static io.lenar.easy.log.support.PJPSupport.getMethodSignatureAsString;
import static io.lenar.easy.log.support.PJPSupport.isVoid;
import static io.lenar.easy.log.support.SerializationSupport.objectToString;
import static io.lenar.easy.log.support.SerializationSupport.paramsToString;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.lenar.easy.log.annotations.Level;
import io.lenar.easy.log.annotations.LogIt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.LoggerFactory;

public class UneasyLogger {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(UneasyLogger.class);

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
        Object result = proceedWithRetry(jp, annotation);
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

    private void logMethodInvocation(String methodName, Map<String, Object> params, LogIt logIt) {
        String message = "\n-> " + methodName + "\n";
        if (!logIt.label().isEmpty()) message = "\n" + logIt.label() + message;
        if (!params.isEmpty()) {
            if (logIt.style() != AS_IS) {
                message = message + paramsToString(params, logIt.maskFields(), logIt.style().prettyPrint, logIt.style().logNulls) + "\n";
            } else {
                String paramsString = params.entrySet().stream()
                        .map(entry -> entry.getKey() + ": "
                                + ( (entry.getValue() == null) ? "null" : entry.getValue().toString()))
                        .collect(Collectors.joining("\n"));
                message = message + paramsString + "\n";
            }
        }
        log(message, logIt.level());
    }

    private void logMethodReturn(long executionTime, String methodName, boolean isVoid, Object result, LogIt logIt) {
        String message = "\nExecution/Response time:  " + executionTime + "ms\n";
        if (!logIt.label().isEmpty()) message = message + logIt.label() + "\n";
        message = message + "<- " + methodName + "\n";
        if (!isVoid) {
            if (logIt.style() != AS_IS) {
                message = message + objectToString(result, logIt.maskFields(), logIt.style().prettyPrint, logIt.style().logNulls) + "\n";
            } else {
                message = message + result.toString() + "\n";
            }
        }
        log(message, logIt.level());
    }

    private Object proceedWithRetry(ProceedingJoinPoint jp, LogIt logIt) throws Throwable {
        int attempts = logIt.retryAttempts() < 0 ? 0 : logIt.retryAttempts();
        long delay = logIt.retryDelay() < 0 ? 0 : logIt.retryDelay();
        try {
            return jp.proceed(jp.getArgs());
        } catch (Throwable ex) {
            if (attempts >= 1 && isRetryException(ex, logIt)) {
                for (int attempt = 1; attempt <= attempts; attempt++) {
                    try {
                        logRetry(jp, attempt, delay, attempts, logIt.label(), ex);
                        Thread.sleep(delay);
                        return jp.proceed(jp.getArgs());
                    } catch (Throwable retryException) {
                        if (!isRetryException(retryException, logIt)) {
                            throw  retryException;
                        }
                    }
                }
            }

            throw ex;
        }
    }

    private void logRetry(ProceedingJoinPoint jp, int attempt, long delay, int attempts, String label, Throwable ex) {
        logger.error("{} \r\n{} <- {}\r\nRetry {}/{} in {} ms",
                ex.toString(),
                label,
                jp.getSignature().toShortString(),
                attempt,
                attempts,
                delay);
    }

    private boolean isRetryException(Throwable exception, LogIt logIt) {
        return Stream.of(logIt.retryExceptions()).anyMatch(item -> item.isAssignableFrom(exception.getClass()));
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
