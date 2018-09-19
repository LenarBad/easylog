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
