package io.lenar.easy.log.support;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class PJPSupport {

    /**
     * This reads names and values of all parameters from
     * ProceedingJoinPoint jp as a map
     */
    public static Map<String, Object> getMethodParameters(ProceedingJoinPoint jp, String[] ignoreList) {
        String[] keys = ((MethodSignature) jp.getSignature()).getParameterNames();
        Object[] values = jp.getArgs();

        Map<String, Object> params = new HashMap<>();
        IntStream.range(0, keys.length).boxed().forEach(i -> {
            if (!isIgnored(ignoreList, keys[i])) params.put(keys[i], values[i]);
        });
        return params;
    }

    /**
     * Retutns method signature as a String
     * Example:
     *          public BookResponse BookServiceClient.createBook(Book book)
     *
     * @param jp ProceedingJoinPoint
     * @param ignoreList List of parameters that shouldn't be logged
     * @param showModifier true if we want to see modifiers like public, private in the method signature
     * @return
     */
    public static String getMethodSignatureAsString(ProceedingJoinPoint jp, boolean showModifier, String[] ignoreList) {
        MethodSignature methodSignature = getMethodSignature(jp);
        String returnedType = methodSignature.getReturnType().getSimpleName();
        String signature = methodSignature.toShortString();
        String[] names = methodSignature.getParameterNames();
        Class[] types = methodSignature.getParameterTypes();
        if (names == null || names.length == 0) {
            signature = signature.replace("..", "");
        } else {
            String params = "";
            for (int i = 0; i < names.length; i++) {
                params = params + types[i].getSimpleName() + " " + names[i];
                if (isIgnored(ignoreList, names[i])) params = params + "<NOT_LOGGED>";
                if (i < names.length - 1) params = params + ", ";
            }
            signature = signature.replace("..", params);
        }
        signature = returnedType + " " + signature;
        if (showModifier) signature = Modifier.toString(methodSignature.getModifiers()) + " " + signature;
        return signature;
    }

    public static boolean isVoid(ProceedingJoinPoint jp) {
        return getMethodSignature(jp).getReturnType().getSimpleName().equals("void");
    }

    private static boolean isIgnored(String[] ignoreList, String parameterName) {
        return ignoreList.length != 0 && Arrays.asList(ignoreList).contains(parameterName);
    }

    private static MethodSignature getMethodSignature(ProceedingJoinPoint jp) {
        return ((MethodSignature) jp.getSignature());
    }


}