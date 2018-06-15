package io.lenar.easy.log.support;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.lenar.easy.log.Level;
import io.lenar.easy.log.annotations.LogIt;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JoinPointLogger extends JoinPointSupport {

    private Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private static Logger logger = LoggerFactory.getLogger("EasyLogger");

    protected Object logMethod(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        logMethodInvocation(
                getMethodSignatureAsString(jp, true, annotation.ignoreParameters()),
                getMethodParameters(jp, annotation.ignoreParameters()), annotation);

        long startTime = System.currentTimeMillis();
        Object result = jp.proceed(jp.getArgs());
        long endTime = System.currentTimeMillis();

        logMethodReturn(
                endTime - startTime,
                getMethodSignatureAsString(jp, false, annotation.ignoreParameters()),
                isVoid(jp), result, annotation);

        return result;
    }

    private void logMethodInvocation(String methodName, Map<String, Object> params, LogIt annotation) {
        String message = "\n-> " + methodName + "\n";
        if (!annotation.label().isEmpty()) message = "\n" + annotation.label() + message;
        if (!params.isEmpty()) message = message + gson.toJson(params) + "\n";
        log(message, annotation.level());
    }

    private void logMethodReturn(long executionTime, String methodName, boolean isVoid, Object result, LogIt annotation) {
        String message = "\nExecution/Response time:  " + executionTime + "ms\n";
        if (!annotation.label().isEmpty()) message = message + annotation.label() + "\n";
        message = message + "<- " + methodName + "\n";
        if (!isVoid) message = message + logObjectNew(result, annotation.maskFields()) + "\n";
        log(message, annotation.level());
    }

    private String logObject(Object object, String[] maskFields) {
        Object cloned = cloneObject(object);
        for (String fieldName : maskFields) {
            try {
//                System.out.println(fieldName + ":" + );
                Field field = cloned.getClass().getField(fieldName);
                if (!field.isAccessible()) field.setAccessible(true);
                if (field.getType() == String.class && field.get(cloned) != null) {
                    System.out.println("!!!!!!!!!!" + "");
                    field.set(cloned, "XXXMASKEDXXX");
                }
            } catch (NoSuchFieldException ex) {
                System.out.println("Couldn't find field " + fieldName);
                // do nothing
            } catch (IllegalAccessException e) {
                // do nothing
                System.out.println("IllegalAccessException field " + fieldName);

            }
        }
        return gson.toJson(cloned);
    }

    private String logObjectNew(Object object, String[] maskFields) {

        Type itemsMapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> map = gson.fromJson(gson.toJson(object), itemsMapType);
        Map<String, Object> newMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (Arrays.asList(maskFields).contains(entry.getKey()) && entry.getValue().getClass() == String.class) {
                newMap.put(entry.getKey(), "XXXMASKEDXXX");
            } else {
                newMap.put(entry.getKey(), entry.getValue());
            }
        }
        return gson.toJson(newMap);
    }

    private void log(String message, Level level) {
        switch (level) {
            case DEBUG: logger.debug(message); break;
            case INFO: logger.info(message); break;
            case WARN: logger.warn(message); break;
            case ERROR: logger.error(message);
        }
    }

    private Object cloneObject(Object originalObject) {
        return gson.fromJson(gson.toJson(originalObject), originalObject.getClass());
    }

}
