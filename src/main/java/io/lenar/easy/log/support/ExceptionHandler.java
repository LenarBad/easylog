package io.lenar.easy.log.support;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.LoggerFactory;

public class ExceptionHandler {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    public static Object proceedExceptionSafe(ProceedingJoinPoint jp) throws Throwable {
        Object result;
        try{
            result = jp.proceed(jp.getArgs());
        }catch(Exception e){
            logger.error("Uncaught Exception", e);
            throw(e);
        }
        return result;
    }

}
