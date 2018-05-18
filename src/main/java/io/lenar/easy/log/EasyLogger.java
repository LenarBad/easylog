package io.lenar.easy.log;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Aspect
public class EasyLogger {

    private Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* *(..)) && @annotation(com.rei.easy.log.LogCall)")
    public Object logCalls(ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().getName();
        String serviceName = ((MethodSignature) jp.getSignature()).getMethod().getAnnotation(LogCall.class).name();
        Map<String, Object> request = getMethodParameters(jp);

        logger.info(serviceName + " CALL:");
        logger.info("-> " + methodName + " Request \n" + gson.toJson(request) + "\n");

        int startTime = DateTime.now().getMillisOfDay();
        Object result = jp.proceed(jp.getArgs());

        int endTime = DateTime.now().getMillisOfDay();

        logger.info("Response time: " + (endTime - startTime) + "ms");
        logger.info("<- " + methodName + " Response: \n" + gson.toJson(result) + "\n");

        return result;
    }

    @Around("execution(* *(..)) && @annotation(com.rei.easy.log.LogMethod)")
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

    /**
     * This reads names and values of all parameters from
     * ProceedingJoinPoint jp as a map
     */
    private Map<String, Object> getMethodParameters(ProceedingJoinPoint jp) {
        String[] keys = ((MethodSignature) jp.getSignature()).getParameterNames();
        Object[] values = jp.getArgs();

        Map<String, Object> params = new HashMap<>();
        IntStream.range(0, keys.length).boxed().forEach(i -> params.put(keys[i], values[i]));
        return params;
    }

}
