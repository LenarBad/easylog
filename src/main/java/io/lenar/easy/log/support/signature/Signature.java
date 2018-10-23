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
package io.lenar.easy.log.support.signature;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Signature {

    private Boolean isInterface;

    private String methodName;
    private Class<?>[] methodParameterTypes;
    private String[] methodParameterNames;
    private String returnedType;
    private Boolean isVoid;

    public ProceedingJoinPoint jp;
    protected MethodSignature methodSignature;
    private Method targetMethod;

    public Signature(ProceedingJoinPoint jp) {
        this.jp = jp;
    }

    protected MethodSignature methodSignature() {
        if (methodSignature == null) {
            methodSignature = (MethodSignature) jp.getSignature();
        }
        return methodSignature;
    }

    protected String methodName() {
        if (methodName == null) {
            methodName = methodSignature().getMethod().getName();
        }
        return methodName;
    }

    public Class<?>[] methodParameterTypes() {
        if (methodParameterTypes == null) {
            methodParameterTypes = methodSignature().getMethod().getParameterTypes();
        }
        return methodParameterTypes;
    }

    public String[] methodParameterNames() {
        if (methodParameterNames != null) return methodParameterNames;
        if (isInterface()) {
            methodParameterNames = Arrays.stream(targetMethod().getParameters())
                    .map(parameter -> parameter.getName())
                    .collect(Collectors.toList())
                    .toArray(new String[targetMethod().getParameters().length]);
        } else {
            methodParameterNames = methodSignature().getParameterNames();
        }
        return methodParameterNames;
    }

    public Method targetMethod() {
        if (targetMethod == null) {
            try {
                targetMethod = jp.getTarget().getClass().getDeclaredMethod(methodSignature().getMethod().getName(), methodParameterTypes());
            } catch (NoSuchMethodException e) {
                //do nothing
            }
        }
        return targetMethod;
    }

    public boolean isInterface() {
        if (isInterface == null) {
            isInterface = jp.getSignature().getDeclaringType().isInterface();
        }
        return isInterface;
    }

    public String returnedType() {
        if (returnedType == null) {
            returnedType = methodSignature().getReturnType().getSimpleName();
        }
        return returnedType;
    }

    public boolean isVoid() {
        if (isVoid == null) {
            isVoid = returnedType().equals("void");
        }
        return isVoid;
    }

    private Object[] getArgs() {
        return jp.getArgs();
    }

    /**
     * This reads names and values of all parameters from
     * ProceedingJoinPoint jp as a map
     */
    public Map<String, Object> getMethodParameters(String[] ignoreList) {
        String[] keys = methodParameterNames();
        Object[] values = getArgs();

        Map<String, Object> params = new HashMap<>();
        IntStream.range(0, keys.length).boxed().forEach(i -> {
            if (!isInArray(ignoreList, keys[i])) params.put(keys[i], values[i]);
        });
        return params;
    }



    protected boolean isInArray(String[] array, String parameterName) {
        return array.length != 0 && Arrays.asList(array).contains(parameterName);
    }
}
