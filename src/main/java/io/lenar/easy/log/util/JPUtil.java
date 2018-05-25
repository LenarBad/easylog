package io.lenar.easy.log.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class JPUtil {

    public static Method getMethod(ProceedingJoinPoint jp) {
        return ((MethodSignature) jp.getSignature()).getMethod();
    }

    /**
     * This reads names and values of all parameters from
     * ProceedingJoinPoint jp as a map
     */
    public static Map<String, Object> getMethodParameters(ProceedingJoinPoint jp) {
        String[] keys = ((MethodSignature) jp.getSignature()).getParameterNames();
        Object[] values = jp.getArgs();

        Map<String, Object> params = new HashMap<>();
        IntStream.range(0, keys.length).boxed().forEach(i -> params.put(keys[i], values[i]));
        return params;
    }

}
