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

import java.lang.reflect.Method;

public class AnnotatedSignature extends Signature {

    public AnnotatedSignature(ProceedingJoinPoint jp) {
        super(jp);
    }

    public AnnotatedSignature(ProceedingJoinPoint jp, LogIt annotation, boolean methodLevel) {
        super(jp);
        if (methodLevel) {
            targetMethodAnnotation = annotation;
            hasTargetMethodAnnotation = true;
        } else {
            targetClassAnnotation = annotation;
            hasTargetClassAnnotation = true;
        }
    }

    private LogIt targetMethodAnnotation;
    private Boolean hasTargetMethodAnnotation;

    private LogIt targetClassAnnotation;
    private Boolean hasTargetClassAnnotation;

    private Boolean hasMethodLevelLogItAnnotation;

    public boolean hasTargetMethodAnnotation() {
        if (hasTargetMethodAnnotation == null) {
            if (jp.getTarget() == null) {
                // for non-Spring apps
                hasTargetMethodAnnotation = methodSignature().getMethod().isAnnotationPresent(LogIt.class);
                return hasTargetMethodAnnotation;
            }
            try {
                hasTargetMethodAnnotation = false;
                Method targetMethod = jp.getTarget().getClass().getDeclaredMethod(methodName(), methodParameterTypes());
                hasTargetMethodAnnotation = targetMethod.isAnnotationPresent(LogIt.class);
            } catch (NoSuchMethodException e) {}
        }
        return hasTargetMethodAnnotation;
    }

    public boolean hasTargetClassAnnotation() {
        if (hasTargetClassAnnotation == null) {
            if (jp.getTarget() == null) {
                // for non-Spring apps
                hasTargetClassAnnotation = jp.getSignature().getDeclaringType().isAnnotationPresent(LogIt.class);
                return hasTargetClassAnnotation;
            }
            hasTargetClassAnnotation = jp.getTarget().getClass().isAnnotationPresent(LogIt.class);
        }
        return hasTargetClassAnnotation;
    }



    public boolean hasMethodLevelLogItAnnotation() {
        if (hasMethodLevelLogItAnnotation != null) return hasMethodLevelLogItAnnotation;
        hasMethodLevelLogItAnnotation = methodSignature().getMethod().isAnnotationPresent(LogIt.class);
        return hasMethodLevelLogItAnnotation;
    }

    protected LogIt targetClassAnnotation() {
        if (targetClassAnnotation == null && hasTargetClassAnnotation()) {
            targetClassAnnotation = jp.getTarget().getClass().getAnnotation(LogIt.class);
        }
        return targetClassAnnotation;
    }

    protected LogIt targetMethodAnnotation() {
        if (targetMethodAnnotation == null && hasTargetMethodAnnotation()) {
            try {
                Method targetMethod = jp.getTarget().getClass().getDeclaredMethod(methodName(), methodParameterTypes());
                targetMethodAnnotation = targetMethod.getAnnotation(LogIt.class);
            } catch (NoSuchMethodException e) {
                // do nothing
            }
        }
        return targetMethodAnnotation;
    }

}
