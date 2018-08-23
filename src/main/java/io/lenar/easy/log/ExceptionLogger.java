package io.lenar.easy.log;

import io.lenar.easy.log.annotations.LogIt;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ExceptionLogger {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ExceptionLogger.class);

    public static void logException(JoinPoint jp, LogIt annotation, Throwable e) {
        if (e instanceof WebApplicationException) {
            logWebApplicationException((WebApplicationException) e, jp, annotation.label());
            return;
        }
        logOtherException(e, jp, annotation.label());
    }

    private static void logOtherException(Throwable e, JoinPoint jp, String label) {
        logger.error("{} \r\n{} <- {}: ",
                e.toString(),
                label,
                jp.getSignature().toShortString(),
                e);
    }

    private static void logWebApplicationException(WebApplicationException webApplicationException, JoinPoint jp, String label) {
        Response response = webApplicationException.getResponse();
        boolean hasResponseBody;
        try {
            hasResponseBody = response.hasEntity();
        } catch (IllegalStateException ise) {
            hasResponseBody = false;
        }
        if (hasResponseBody) {
            response.bufferEntity();
            logger.error("{}\r\n{} <- {}: \r\n{}",
                    webApplicationException.toString(),
                    label,
                    jp.getSignature().toShortString(),
                    response.readEntity(String.class));
        } else {
            logger.error("{}\r\n{} <- {}: \r\n",
                    webApplicationException.toString(),
                    label,
                    jp.getSignature().toShortString());
        }
    }

}
