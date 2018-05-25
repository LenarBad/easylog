package io.lenar.easy.log;

import static io.lenar.easy.log.util.JPUtil.getMethod;
import static io.lenar.easy.log.util.JPUtil.getMethodParameters;

import java.lang.reflect.Method;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Aspect
public class EasyLogger {

    private Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    protected static Logger logger = LoggerFactory.getLogger("EasyLogger");

    @Around("execution(* *(..)) && @annotation(io.lenar.easy.log.LogCall)")
    public Object logCalls(ProceedingJoinPoint jp) throws Throwable {
        Method method = getMethod(jp);

        logRequest(method.getAnnotation(LogCall.class).name(), method.getName(), getMethodParameters(jp));

        int startTime = DateTime.now().getMillisOfDay();
        Object result = jp.proceed(jp.getArgs());
        int endTime = DateTime.now().getMillisOfDay();

        logResponse(endTime - startTime, getMethod(jp).getName(), result);

        return result;
    }

    @Around("execution(* *(..)) && @annotation(io.lenar.easy.log.LogMethod)")
    public Object logMethods(ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().getName();
        Map<String, Object> params = getMethodParameters(jp);

        if (params.isEmpty()) {
            logger.info("-> Method " + jp.getSignature().getName() + " with no parameters");
        } else {
            logger.info("-> Method " + jp.getSignature().getName() + " with parameters \n" + gson.toJson(params) + "\n");
        }

        int startTime = DateTime.now().getMillisOfDay();
        Object result = jp.proceed(jp.getArgs());

        int endTime = DateTime.now().getMillisOfDay();

        logger.info("Execution time: " + (endTime - startTime) + "ms");
        logger.info("<- " + methodName + " returned: \n" + gson.toJson(result) + "\n");

        return result;
    }

    private void logRequest(String serviceName, String methodName, Map<String, Object> request) {
        logger.info(requestMessage(serviceName, methodName, request));
    }

    private String requestMessage(String serviceName, String methodName, Map<String, Object> request) {
        String message = "\n";
        if (!serviceName.isEmpty()) message = message + serviceName + " CALL:\n";
        return message + "-> " + methodName + " Request \n" + gson.toJson(request) + "\n";
    }

    private void logResponse(int responseTime, String methodName, Object result) {
        logger.info(responseMessage(responseTime, methodName, result));
    }

    private String responseMessage(int responseTime, String methodName, Object result) {
        return "Response time: " + responseTime + "ms\n" + "<- " + methodName + " Response: \n" + gson.toJson(result) + "\n";
    }

}
