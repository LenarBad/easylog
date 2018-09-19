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
package io.lenar.easy.log.support;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import io.lenar.easy.log.annotations.LogIt;
import org.aspectj.lang.JoinPoint;
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
            if (!isInArray(ignoreList, keys[i])) params.put(keys[i], values[i]);
        });
        return params;
    }

    /**
     * Retutns method signature as a String
     * Example:
     *          public BookResponse BookServiceClient.createBook(Book book)
     *
     * @param jp ProceedingJoinPoint
     * @param showModifier true if we want to see modifiers like public, private in the method signature
     * @param ignoreList List of parameters that shouldn't be logged
     * @param maskFields List of parameters that should be masked
     * @return
     */
    public static String getMethodSignatureAsString(
            ProceedingJoinPoint jp,
            boolean showModifier,
            String[] ignoreList,
            String[] maskFields) {
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
                if (isInArray(ignoreList, names[i])) {
                    params = params + "<NOT_LOGGED>";
                } else {
                    if (isInArray(maskFields, names[i])) {
                        params = params + "<MASKED>";
                    }
                }
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

    public static boolean hasMethodLevelLogItAnnotation(JoinPoint jp) {
        if (((MethodSignature) jp.getSignature()).getMethod().getAnnotationsByType(LogIt.class).length != 0) {
            return true;
        }
        return false;
    }

    private static boolean isInArray(String[] array, String parameterName) {
        return array.length != 0 && Arrays.asList(array).contains(parameterName);
    }

    private static MethodSignature getMethodSignature(ProceedingJoinPoint jp) {
        return ((MethodSignature) jp.getSignature());
    }

}
