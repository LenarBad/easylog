package io.lenar.easy.log;

import io.lenar.easy.log.annotations.LogIt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static io.lenar.easy.log.support.PJPSupport.hasMethodLevelLogItAnnotation;

@Aspect
public class InterfaceLogger extends UneasyLogger {

    protected static final String PACKAGE = "*";

    @Pointcut("execution(* *.*+.*(..))")
    public void anyMethodOfAnyImplementationOfAnyInterface() {}

    @Around("execution(* *.*+.*(..))")
    public Object logThat(ProceedingJoinPoint jp) throws Throwable {
        LogIt annotation = null;

        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        final Class<?> clazz = jp.getTarget().getClass();

        Class<?>[] interfacesOfClazz = clazz.getInterfaces();

        for (Class<?> interfaceOfClazz : interfacesOfClazz) {
            Method iMethod;
            try {
                iMethod = interfaceOfClazz.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                continue;
            } catch (SecurityException e) {
                continue;
            }

            annotation = interfaceOfClazz.getAnnotation(LogIt.class);
            if (annotation != null) {
                return annotation;
            }
            annotation = iMethod.getAnnotation(LogIt.class);
            if (annotation != null) {
                return annotation;
            }

        }

        if (annotation != null) {
           System.out.println( method.getName() + " !!!!!!!!!!!!!!!!!!!!!!!   !!!!!!!!!!!!!!!!!!!!!!!!");
           return logMethod(jp, annotation);
        }


        System.out.println("!!!!!!!!!!!!!!!!!   !!!!!!!!!!!!!!!!!!!!!!!   !!!!!!!!!!!!!!!!!!!!!!!!");
        return jp.proceed(jp.getArgs());
    }


}
