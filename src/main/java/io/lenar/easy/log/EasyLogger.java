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
package io.lenar.easy.log;

import io.lenar.easy.log.annotations.LogIt;

import io.lenar.easy.log.support.signature.AnnotatedInterfaceSignature;
import io.lenar.easy.log.support.signature.EasyLogSignature;
import io.lenar.easy.log.support.signature.JPSignature;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import static io.lenar.easy.log.ExceptionLogger.logException;

@Aspect
public class EasyLogger extends UneasyLogger {

    @Pointcut("execution(* *(..))")
    public void anyMethod() {}

    @Around("anyMethod() && @within(annotation)")
    public Object logItClassLevel(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        EasyLogSignature signature = new EasyLogSignature(new JPSignature(jp));
//        AnnotatedInterfaceSignature signature = new AnnotatedInterfaceSignature(jp, annotation, false);
//        if (signature.hasTargetMethodAnnotation()) {
//            return jp.proceed(jp.getArgs());
//        }
        if (signature.hasMethodLevelAnnotation()) {
            return jp.proceed(jp.getArgs());
        }
        return logMethod(signature, jp);
    }

    @Around("anyMethod() && @annotation(annotation)")
    public Object logItMethodLevel(ProceedingJoinPoint jp, LogIt annotation) throws Throwable {
        EasyLogSignature signature = new EasyLogSignature(new JPSignature(jp));
//        AnnotatedInterfaceSignature signature = new AnnotatedInterfaceSignature(jp, annotation, true);
        return logMethod(signature, jp);
    }

    @AfterThrowing(pointcut = "anyMethod() && @within(annotation)", throwing = "e")
    public void logExceptionClassLevel(JoinPoint  jp, LogIt annotation, Throwable e) {
        AnnotatedInterfaceSignature signature = new AnnotatedInterfaceSignature((ProceedingJoinPoint) jp, annotation, false);
        if (!signature.hasTargetMethodAnnotation()) {
            logException(jp, annotation, e);
        }
    }

    @AfterThrowing(pointcut = "anyMethod() && @annotation(annotation)", throwing = "e")
    public void logExceptionMethodLevel(JoinPoint jp, LogIt annotation, Throwable e) {
        logException(jp, annotation, e);
    }

}
