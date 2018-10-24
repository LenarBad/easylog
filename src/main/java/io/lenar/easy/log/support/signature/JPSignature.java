package io.lenar.easy.log.support.signature;

import io.lenar.easy.log.annotations.LogIt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The class contains all the information about the joint point that it's created for.
 * We fill all the fields at the first call time for each joint point and then don't use any joint point related call except jp.getArgs().
 * Doing this we expect to have improved performance after the first call.
 */
public class JPSignature {

    private String signatureId;
    private boolean isAnnotated;
    private LogIt effectiveAnnotation;
    private String[] paramNames;
    private Class<?>[] paramTypes;
    private String returnedType;
    private boolean isVoid;
    private boolean isInterface;
    private boolean isJavaxWsRsInterface;
    private boolean hasMethodLevelAnnotation;
    private LogIt methodLevelAnnotation;
    private boolean hasClassLevelAnnotation;
    private LogIt classLevelAnnotation;
    private boolean hasInterfaceMethodLevelAnnotation;
    private LogIt interfaceMethodLevelAnnotation;
    private boolean hasInterfaceLevelAnnotation;
    private LogIt interfaceLevelAnnotation;


    private String methodSignatureWithModifiers;
    private String methodSignatureWithoutModifiers;


    /**
     * Private fields that are not supposed to be exposed externally (even via getters)
     */
    private MethodSignature methodSignature;

    public JPSignature(ProceedingJoinPoint jp) {
        signatureId = jp.toLongString();
        methodSignature = (MethodSignature) jp.getSignature();
        returnedType = methodSignature.getReturnType().getSimpleName();
        isVoid = returnedType.equals("void");
        paramTypes = methodSignature.getMethod().getParameterTypes();

        setClassAnnotations(jp);
        setInterfaceAnnotations(jp);
        setEffectiveAnnotation();

        isJavaxWsRsInterface = jp.getSignature().getDeclaringType().isAnnotationPresent(javax.ws.rs.Path.class)
                || methodSignature.getMethod().isAnnotationPresent(javax.ws.rs.Path.class);

        methodSignatureWithModifiers = getMethodSignatureAsString(jp, true);
        methodSignatureWithoutModifiers = getMethodSignatureAsString(jp, false);
    }

    public String methodSignatureWithModifiers() {
        return methodSignatureWithModifiers;
    }

    public String methodSignatureWithoutModifiers() {
        return methodSignatureWithoutModifiers;
    }

    public boolean isAnnotated() {
        return isAnnotated;
    }

    public LogIt effectiveAnnotation() {
        return effectiveAnnotation;
    }

    public String returnedType() {
        return returnedType;
    }

    public boolean isVoid() {
        return isVoid;
    }

    public String[] paramNames() {
        return paramNames;
    }

    public Class<?>[] paramTypes() {
        return paramTypes;
    }

    public boolean hasMethodLevelAnnotation() {
        return hasMethodLevelAnnotation;
    }

    public boolean hasClassLevelAnnotation() {
        return hasClassLevelAnnotation;
    }

    public boolean hasInterfaceMethodLevelAnnotation() {
        return hasInterfaceMethodLevelAnnotation;
    }

    public boolean hasInterfaceLevelAnnotation() {
        return hasInterfaceLevelAnnotation;
    }

    private void setClassAnnotations(ProceedingJoinPoint jp) {
        String methodName = methodSignature.getMethod().getName();
        Method targetMethod = null;
        if (jp.getTarget() == null) {
            hasMethodLevelAnnotation = methodSignature.getMethod().isAnnotationPresent(LogIt.class);
            if (hasMethodLevelAnnotation) {
                methodLevelAnnotation = methodSignature.getMethod().getAnnotation(LogIt.class);
            }
            hasClassLevelAnnotation = jp.getSignature().getDeclaringType().isAnnotationPresent(LogIt.class);
            if (hasClassLevelAnnotation) {
                classLevelAnnotation = (LogIt) jp.getSignature().getDeclaringType().getAnnotation(LogIt.class);
            }
        } else {
            try {
                targetMethod = jp.getTarget().getClass().getDeclaredMethod(methodName, paramTypes);
                hasMethodLevelAnnotation = targetMethod.isAnnotationPresent(LogIt.class);
                if (hasMethodLevelAnnotation) {
                    methodLevelAnnotation = targetMethod.getAnnotation(LogIt.class);
                }
                hasClassLevelAnnotation = jp.getTarget().getClass().isAnnotationPresent(LogIt.class);
                if (hasClassLevelAnnotation) {
                    classLevelAnnotation = jp.getTarget().getClass().getAnnotation(LogIt.class);
                }
            } catch (NoSuchMethodException e) { }
        }
    }

    private void setInterfaceAnnotations(ProceedingJoinPoint jp) {
        String methodName = methodSignature.getMethod().getName();

        isInterface = jp.getSignature().getDeclaringType().isInterface();
        if (isInterface) {
            Method targetMethod = null;
            try {
                targetMethod = jp.getTarget().getClass().getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) { }
            paramNames = Arrays.stream(targetMethod.getParameters())
                    .map(parameter -> parameter.getName())
                    .collect(Collectors.toList())
                    .toArray(new String[targetMethod.getParameters().length]);
            hasInterfaceMethodLevelAnnotation = methodSignature.getMethod().isAnnotationPresent(LogIt.class);
            if (hasInterfaceMethodLevelAnnotation) {
                interfaceMethodLevelAnnotation = methodSignature.getMethod().getAnnotation(LogIt.class);
            }
            hasInterfaceLevelAnnotation = jp.getSignature().getDeclaringType().isAnnotationPresent(LogIt.class);
            if (hasInterfaceLevelAnnotation) {
                interfaceLevelAnnotation = (LogIt) jp.getSignature().getDeclaringType().getAnnotation(LogIt.class);
            }
        } else {
            paramNames = methodSignature.getParameterNames();
            hasInterfaceMethodLevelAnnotation = Arrays.stream(jp.getSignature().getDeclaringType().getInterfaces())
                    .map(anInterface -> {
                        try {
                            return anInterface.getDeclaredMethod(methodName, paramTypes);
                        } catch (NoSuchMethodException e) {
                            return null;
                        }
                    })
                    .anyMatch(interfaceMethod -> interfaceMethod != null && interfaceMethod.isAnnotationPresent(LogIt.class));
            if (hasInterfaceMethodLevelAnnotation) {
                interfaceMethodLevelAnnotation = Arrays.stream(jp.getSignature().getDeclaringType().getInterfaces())
                        .map(anInterface -> {
                            try {
                                return anInterface.getDeclaredMethod(methodName, paramTypes);
                            } catch (NoSuchMethodException e) {
                                return null;
                            }
                        })
                        .filter(interfaceMethod -> interfaceMethod != null && interfaceMethod.isAnnotationPresent(LogIt.class))
                        .map(interfaceMethod -> interfaceMethod.getAnnotation(LogIt.class)).findFirst().orElse(null);
            }
            hasInterfaceLevelAnnotation = Arrays.stream(jp.getSignature().getDeclaringType().getInterfaces())
                    .anyMatch(anInterface -> anInterface.isAnnotationPresent(LogIt.class));
            if (hasInterfaceLevelAnnotation) {
                interfaceLevelAnnotation = (LogIt) Arrays.stream(jp.getSignature().getDeclaringType().getInterfaces())
                        .filter(anInterface -> anInterface.isAnnotationPresent(LogIt.class))
                        .map(aClass -> aClass.getAnnotation(LogIt.class)).findFirst().orElse(null);
            }
        }

    }

    public void setEffectiveAnnotation() {
        effectiveAnnotation = null;
        if (hasMethodLevelAnnotation) {
            effectiveAnnotation = methodLevelAnnotation;
        } else {
            if (hasClassLevelAnnotation) {
                effectiveAnnotation = classLevelAnnotation;
            } else {
                if (hasInterfaceMethodLevelAnnotation) {
                    effectiveAnnotation = interfaceMethodLevelAnnotation;
                } else {
                    if (hasInterfaceLevelAnnotation) {
                        effectiveAnnotation = interfaceLevelAnnotation;
                    }
                }
            }
        }

        if (effectiveAnnotation != null) isAnnotated = true;
    }

    /**
     * This reads names and values of all parameters from
     * ProceedingJoinPoint jp as a map
     */
    public Map<String, Object> getMethodParameters(Object[] values, String[] ignoreList) {
        String[] keys = paramNames;

        Map<String, Object> params = new HashMap<>();
        IntStream.range(0, keys.length).boxed().forEach(i -> {
            if (!isInArray(ignoreList, keys[i])) params.put(keys[i], values[i]);
        });
        return params;
    }

    private boolean isInArray(String[] array, String parameterName) {
        return array.length != 0 && Arrays.asList(array).contains(parameterName);
    }

    private JPSignature methodSignature(ProceedingJoinPoint jp) {
        methodSignature = (MethodSignature) jp.getSignature();
        return this;
    }

    /**
     * Retutns method signature as a String
     * Example:
     *          public BookResponse BookServiceClient.createBook(Book book)
     *
     * @param showModifier true if we want to see modifiers like public, private in the method signature
     * @return
     */
    public String getMethodSignatureAsString(ProceedingJoinPoint jp, boolean showModifier) {
        String stringSignature = methodSignature.toShortString();
        String methodName = methodSignature.getMethod().getName();
        String modifier = showModifier ? Modifier.toString(methodSignature.getModifiers()) + " " : "";

        Method targetMethod = null;
        try {
            targetMethod = jp.getTarget().getClass().getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) { }

        if (isInterface) {
            if (!isJavaxWsRsInterface) {
                stringSignature = jp.getTarget().getClass().getSimpleName() + "." + targetMethod.getName() + "(..)";
            }
            modifier = showModifier ? Modifier.toString(targetMethod.getModifiers()) + " " : "";
        }

        String params = "";

        for (int i = 0; i < paramNames.length; i++) {
            params = params + paramTypes[i].getSimpleName() + " " + paramNames[i];
            if (isInArray(effectiveAnnotation().ignoreParameters(), paramNames[i])) {
                params = params + "<NOT_LOGGED>";
            } else {
                if (isInArray(effectiveAnnotation.maskFields(), paramNames[i])) {
                    params = params + "<MASKED>";
                }
            }
            if (i < paramNames.length - 1) params = params + ", ";
        }
        stringSignature = returnedType + " " + modifier + stringSignature.replace("..", params);
        return stringSignature;
    }

}
