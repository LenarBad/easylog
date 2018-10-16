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

import io.lenar.easy.log.annotations.LogIt;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Modifier;
import java.util.Arrays;

public class AnnotatedInterfaceSignature extends AnnotatedSignature {
    public AnnotatedInterfaceSignature(ProceedingJoinPoint jp) {
        super(jp);
    }

    public AnnotatedInterfaceSignature(ProceedingJoinPoint jp, LogIt annotation, boolean methodLevel) {
        super(jp, annotation, methodLevel);
    }

    private Boolean hasInterfaceMethodAnnotation;
    private LogIt interfaceMethodAnnotation;
    private Boolean hasInterfaceLevelAnnotation;
    private LogIt interfaceLevelAnnotation;
    private LogIt effectiveAnnotation;

    private Boolean isJavaxWsRsInterface;

    public boolean hasInterfaceMethodAnnotation() {
        if (hasInterfaceMethodAnnotation != null) return hasInterfaceMethodAnnotation;
        if (isInterface()) {
            hasInterfaceMethodAnnotation = methodSignature().getMethod().isAnnotationPresent(LogIt.class);
        } else {
            hasInterfaceMethodAnnotation = Arrays.stream(jp.getSignature().getDeclaringType().getInterfaces())
                    .map(anInterface -> {
                        try {
                            return anInterface.getDeclaredMethod(methodName(), methodParameterTypes());
                        } catch (NoSuchMethodException e) {
                            return null;
                        }
                    })
                    .anyMatch(interfaceMethod -> interfaceMethod != null && interfaceMethod.isAnnotationPresent(LogIt.class));
        }
        return hasInterfaceMethodAnnotation;
    }

    public boolean hasInterfaceLevelAnnotation() {
        if (hasInterfaceLevelAnnotation != null) return hasInterfaceLevelAnnotation;
        if (isInterface()) {
            hasInterfaceLevelAnnotation = jp.getSignature().getDeclaringType().isAnnotationPresent(LogIt.class);
        } else {
            hasInterfaceLevelAnnotation = Arrays.stream(jp.getSignature().getDeclaringType().getInterfaces())
                    .anyMatch(anInterface -> anInterface.isAnnotationPresent(LogIt.class));
        }
        return hasInterfaceLevelAnnotation;
    }

    public LogIt interfaceMethodAnnotation() {
        if (interfaceMethodAnnotation != null) return interfaceMethodAnnotation;
        if (isInterface()) {
            interfaceMethodAnnotation = methodSignature().getMethod().getAnnotation(LogIt.class);
        } else {
            interfaceMethodAnnotation = Arrays.stream(jp.getSignature().getDeclaringType().getInterfaces())
                    .map(anInterface -> {
                        try {
                            return anInterface.getDeclaredMethod(methodName(), methodParameterTypes());
                        } catch (NoSuchMethodException e) {
                            return null;
                        }
                    })
                    .filter(interfaceMethod -> interfaceMethod != null && interfaceMethod.isAnnotationPresent(LogIt.class))
                    .map(interfaceMethod -> interfaceMethod.getAnnotation(LogIt.class)).findFirst().orElse(null);
        }
        return interfaceMethodAnnotation;
    }

    public LogIt interfaceLevelAnnotation() {
        if (interfaceLevelAnnotation != null) return interfaceLevelAnnotation;
        if (isInterface()) {
            interfaceLevelAnnotation = (LogIt) jp.getSignature().getDeclaringType().getAnnotation(LogIt.class);
        } else {
            interfaceLevelAnnotation = (LogIt) Arrays.stream(jp.getSignature().getDeclaringType().getInterfaces())
                    .filter(anInterface -> anInterface.isAnnotationPresent(LogIt.class))
                    .map(aClass -> aClass.getAnnotation(LogIt.class)).findFirst().orElse(null);
        }
        return interfaceLevelAnnotation;
    }

    public LogIt effectiveAnnotation() {
        if (effectiveAnnotation != null) return effectiveAnnotation;
        if (hasTargetMethodAnnotation()) {
            effectiveAnnotation = targetMethodAnnotation();
            return effectiveAnnotation;
        }
        if (hasTargetClassAnnotation()) {
            effectiveAnnotation = targetClassAnnotation();
            return effectiveAnnotation;
        }
        if (hasInterfaceMethodAnnotation()) {
            effectiveAnnotation = interfaceMethodAnnotation();
            return effectiveAnnotation;
        }
        if (hasInterfaceLevelAnnotation()) {
            effectiveAnnotation = interfaceLevelAnnotation();
            return effectiveAnnotation;
        }
        return effectiveAnnotation;
    }

    public boolean isJavaxWsRsInterface() {
        if (isJavaxWsRsInterface != null) return isJavaxWsRsInterface;
        isJavaxWsRsInterface = jp.getSignature().getDeclaringType().isAnnotationPresent(javax.ws.rs.Path.class)
                || methodSignature().getMethod().isAnnotationPresent(javax.ws.rs.Path.class);
        return isJavaxWsRsInterface;
    }

    /**
     * Retutns method signature as a String
     * Example:
     *          public BookResponse BookServiceClient.createBook(Book book)
     *
     * @param showModifier true if we want to see modifiers like public, private in the method signature
     * @return
     */
    public String getMethodSignatureAsString(boolean showModifier) {
        String stringSignature = methodSignature().toShortString();
        String[] names = methodParameterNames();
        Class[] types = methodParameterTypes();
        String modifier = showModifier ? Modifier.toString(methodSignature().getModifiers()) + " " : "";

        if (isInterface()) {
            if (!isJavaxWsRsInterface()) {
                stringSignature = jp.getTarget().getClass().getSimpleName() + "." + targetMethod().getName() + "(..)";
            }
            modifier = showModifier ? Modifier.toString(targetMethod().getModifiers()) + " " : "";
        }

        String params = "";

        for (int i = 0; i < names.length; i++) {
            params = params + types[i].getSimpleName() + " " + names[i];
            if (isInArray(effectiveAnnotation().ignoreParameters(), names[i])) {
                params = params + "<NOT_LOGGED>";
            } else {
                if (isInArray(effectiveAnnotation.maskFields(), names[i])) {
                    params = params + "<MASKED>";
                }
            }
            if (i < names.length - 1) params = params + ", ";
        }
        stringSignature = returnedType() + " " + modifier + stringSignature.replace("..", params);
        return stringSignature;
    }

}
