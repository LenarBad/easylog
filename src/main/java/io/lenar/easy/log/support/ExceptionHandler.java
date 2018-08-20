package io.lenar.easy.log.support;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.LoggerFactory;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ExceptionHandler {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    public static Object proceedExceptionSafe(ProceedingJoinPoint jp, String label) throws Throwable {
        Object result;
        try{
            result = jp.proceed(jp.getArgs());
        }catch (WebApplicationException webApplicationException){
            logWebApplicationException(webApplicationException, jp, label);
            throw(webApplicationException);
        } catch(Exception e){
            logger.error("Uncaught Exception", e);
            throw(e);
        }
        return result;
    }

    private static void logWebApplicationException(WebApplicationException webApplicationException, ProceedingJoinPoint jp, String label) {
        Response response = webApplicationException.getResponse();
        boolean hasResponseBody;
        try {
            hasResponseBody = response.hasEntity();
        } catch (IllegalStateException ise) {
            hasResponseBody = false;
        }
        if (hasResponseBody) {
            response.bufferEntity();
            logger.info("{} in \r\n{} <- {}: \r\n{}",
                    webApplicationException.toString(),
                    label,
                    jp.getSignature().toShortString(),
                    response.readEntity(String.class));
        } else {
            logger.info("{} in \r\n{} <- {}: \r\n",
                    webApplicationException.toString(),
                    label,
                    jp.getSignature().toShortString());
        }
    }

}
