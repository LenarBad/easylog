/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Lenar Badretdinov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.lenar.easy.log;

import static io.lenar.easy.log.annotations.Style.AS_IS;
import static io.lenar.easy.log.support.SerializationSupport.objectToString;
import static io.lenar.easy.log.support.SerializationSupport.paramsToString;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.lenar.easy.log.annotations.Level;
import io.lenar.easy.log.annotations.LogIt;
import io.lenar.easy.log.annotations.Style;
import io.lenar.easy.log.support.signature.AnnotatedInterfaceSignature;
import io.lenar.easy.log.support.signature.EasyLogSignature;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.LoggerFactory;

public class UneasyLogger {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(UneasyLogger.class);

//    protected  Object logMethod(AnnotatedInterfaceSignature signature) throws Throwable {
//        LogIt annotation = signature.effectiveAnnotation();
//        try {
//            logMethodInvocation(signature);
//        } catch (Exception ex){
//            log("Failed to process and log method's parameters \r\n" + signature.getMethodSignatureAsString(true),
//                    annotation.level());
//        }
//
//        long startTime = System.currentTimeMillis();
//        Object result = proceedWithRetry(signature.jp, annotation);
//        long endTime = System.currentTimeMillis();
//
//        try {
//            logMethodReturn(
//                    endTime - startTime,
//                    signature.getMethodSignatureAsString(false),
//                    signature.isVoid(),
//                    result,
//                    annotation);
//        } catch (Exception ex) {
//            log("Failed to process and log method's return \r\n" +
//                            signature.getMethodSignatureAsString(false),
//                    annotation.level());
//        }
//
//        return result;
//    }

    protected  Object logMethod(EasyLogSignature signature, ProceedingJoinPoint jp) throws Throwable {
        try {
            logMethodInvocation(signature, jp);
        } catch (Exception ex){
            log("Failed to process and log method's parameters \r\n" + signature.methodSignatureWithModifiers(),
                    signature.level());
        }

        long startTime = System.currentTimeMillis();
        Object result = proceedWithRetry(jp, signature);
        long endTime = System.currentTimeMillis();

        try {
            logMethodReturn(
                    endTime - startTime,
                    signature.methodSignatureWithoutModifiers(),
                    result,
                    signature);
        } catch (Exception ex) {
            log("Failed to process and log method's return \r\n" +
                            signature.methodSignatureWithoutModifiers(),
                    signature.level());
        }

        return result;
    }

    private void logMethodInvocation(EasyLogSignature signature, ProceedingJoinPoint jp) {
        String methodName = signature.methodSignatureWithModifiers();

        Map<String, Object> params = signature.getMethodParameters(jp.getArgs());
        String label = signature.label();
        Style style = signature.style();
        String[] maskFields = signature.maskFields();

        String message = "\r\n-> " + methodName + "\r\n";
        if (!label.isEmpty()) message = "\r\n" + label + message;
        if (!params.isEmpty()) {
            if (style != AS_IS) {
                message = message + paramsToString(params, maskFields, style.prettyPrint, style.logNulls) + "\r\n";
            } else {
                String paramsString = params.entrySet().stream()
                        .map(entry -> entry.getKey() + ": "
                                + ( (entry.getValue() == null) ? "null" : entry.getValue().toString()))
                        .collect(Collectors.joining("\r\n"));
                message = message + paramsString + "\r\n";
            }
        }
        log(message, signature.level());
    }

//    private void logMethodInvocation(AnnotatedInterfaceSignature signature) {
//        String methodName = signature.getMethodSignatureAsString(true);
//
//        LogIt annotation = signature.effectiveAnnotation();
//        Map<String, Object> params = signature.getMethodParameters(annotation.ignoreParameters());
//        String label = annotation.label();
//        Style style = annotation.style();
//        String[] maskFields = annotation.maskFields();
//
//        String message = "\r\n-> " + methodName + "\r\n";
//        if (!label.isEmpty()) message = "\r\n" + label + message;
//        if (!params.isEmpty()) {
//            if (style != AS_IS) {
//                message = message + paramsToString(params, maskFields, style.prettyPrint, style.logNulls) + "\r\n";
//            } else {
//                String paramsString = params.entrySet().stream()
//                        .map(entry -> entry.getKey() + ": "
//                                + ( (entry.getValue() == null) ? "null" : entry.getValue().toString()))
//                        .collect(Collectors.joining("\r\n"));
//                message = message + paramsString + "\r\n";
//            }
//        }
//        log(message, annotation.level());
//    }

//    private void logMethodReturn(long executionTime, String methodName, boolean isVoid, Object result, LogIt logIt) {
//        String message = "\nExecution/Response time:  " + executionTime + "ms\n";
//        if (!logIt.label().isEmpty()) message = message + logIt.label() + "\n";
//        message = message + "<- " + methodName + "\n";
//        if (!isVoid) {
//            if (logIt.style() != AS_IS) {
//                message = message + objectToString(result, logIt.maskFields(), logIt.style().prettyPrint, logIt.style().logNulls) + "\n";
//            } else {
//                message = message + result.toString() + "\n";
//            }
//        }
//        log(message, logIt.level());
//    }

    private void logMethodReturn(long executionTime, String methodName, Object result, EasyLogSignature signature) {
        String message = "\nExecution/Response time:  " + executionTime + "ms\n";
        if (!signature.label().isEmpty()) message = message + signature.label() + "\n";
        message = message + "<- " + methodName + "\n";
        if (!signature.isVoid()) {
            if (signature.style() != AS_IS) {
                message = message + objectToString(result, signature.maskFields(), signature.style().prettyPrint, signature.style().logNulls) + "\n";
            } else {
                message = message + result.toString() + "\n";
            }
        }
        log(message, signature.level());
    }

//    private Object proceedWithRetry(ProceedingJoinPoint jp, LogIt logIt) throws Throwable {
//        int attempts = logIt.retryAttempts() < 0 ? 0 : logIt.retryAttempts();
//        long delay = logIt.retryDelay() < 0 ? 0 : logIt.retryDelay();
//        try {
//            return jp.proceed(jp.getArgs());
//        } catch (Throwable ex) {
//            if (attempts >= 1 && isRetryException(ex, logIt)) {
//                for (int attempt = 1; attempt <= attempts; attempt++) {
//                    try {
//                        logRetry(jp, attempt, delay, attempts, logIt.label(), ex);
//                        Thread.sleep(delay);
//                        return jp.proceed(jp.getArgs());
//                    } catch (Throwable retryException) {
//                        if (!isRetryException(retryException, logIt)) {
//                            throw  retryException;
//                        }
//                    }
//                }
//            }
//
//            throw ex;
//        }
//    }

    private Object proceedWithRetry(ProceedingJoinPoint jp, EasyLogSignature signature) throws Throwable {
        int attempts = signature.retryAttempts() < 0 ? 0 : signature.retryAttempts();
        long delay = signature.retryDelay() < 0 ? 0 : signature.retryDelay();
        try {
            return jp.proceed(jp.getArgs());
        } catch (Throwable ex) {
            if (attempts >= 1 && isRetryException(ex, signature)) {
                for (int attempt = 1; attempt <= attempts; attempt++) {
                    try {
                        logRetry(jp, attempt, delay, attempts, signature.label(), ex);
                        Thread.sleep(delay);
                        return jp.proceed(jp.getArgs());
                    } catch (Throwable retryException) {
                        if (!isRetryException(retryException, signature)) {
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

//    private boolean isRetryException(Throwable thrownException, LogIt logIt) {
//        return Stream.of(logIt.retryExceptions())
//                .anyMatch(item -> item.isAssignableFrom(thrownException.getClass()));
//    }

    private boolean isRetryException(Throwable thrownException, EasyLogSignature signature) {
        return Stream.of(signature.retryExceptions())
                .anyMatch(item -> item.isAssignableFrom(thrownException.getClass()));
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
