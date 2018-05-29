package io.lenar.easy.log.support;

import java.lang.reflect.Method;
import java.util.Map;

import io.lenar.easy.log.support.JoinPointSupport;

import org.aspectj.lang.ProceedingJoinPoint;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JoinPointLogger extends JoinPointSupport {

    private Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    protected static Logger logger = LoggerFactory.getLogger("EasyLogger");

    public Object logAsCall(ProceedingJoinPoint jp, String serviceName) throws Throwable {
        Method method = getMethod(jp);

        logRequest(serviceName, method.getName(), getMethodParameters(jp));

        int startTime = DateTime.now().getMillisOfDay();
        Object result = jp.proceed(jp.getArgs());
        int endTime = DateTime.now().getMillisOfDay();

        logResponse(endTime - startTime, method.getName(), result);

        return result;
    }

    public Object logAsMethod(ProceedingJoinPoint jp) throws Throwable {
        Method method = getMethod(jp);

        logMethodInvocation(method.getName(), getMethodParameters(jp));

        int startTime = DateTime.now().getMillisOfDay();
        Object result = jp.proceed(jp.getArgs());
        int endTime = DateTime.now().getMillisOfDay();

        logMethodReturn(endTime - startTime, method.getName(), result);

        return result;
    }

    private void logRequest(String serviceName, String methodName, Map<String, Object> request) {
        String message = "\n";
        if (!serviceName.isEmpty()) message = message + serviceName + " CALL:\n";
        message = message + "-> " + methodName + " Request \n" + gson.toJson(request) + "\n";
        logger.info(message);
    }

    private void logResponse(int responseTime, String methodName, Object result) {
        String message = "Response time: " + responseTime + "ms\n" + "<- " + methodName + " Response: \n" + gson.toJson(result) + "\n";
        logger.info(message);
    }

    private void logMethodInvocation(String methodName, Map<String, Object> params) {
        String message = "\n-> Method " + methodName;
        message = message + (params.isEmpty() ? " without parameters" : " with parameters \n" + gson.toJson(params) + "\n");
        logger.info(message);
    }

    private void logMethodReturn(int executionTime, String methodName, Object result) {
        String message = "Execution time: + " + executionTime + "ms\n";
        message = message + "<- " + methodName + " returned: \n" + gson.toJson(result) + "\n";
        logger.info(message);
    }

}
