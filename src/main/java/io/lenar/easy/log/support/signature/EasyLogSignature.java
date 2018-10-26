package io.lenar.easy.log.support.signature;

import io.lenar.easy.log.annotations.Level;
import io.lenar.easy.log.annotations.LogIt;
import io.lenar.easy.log.annotations.Style;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class EasyLogSignature {

    // Annotation parameters
    private String label;
    private Level level;
    private String[] ignoreParameters;
    private String[] maskFields;
    private Style style;
    private int retryAttempts;
    private long retryDelay;
    private Class<? extends Throwable>[] retryExceptions;

    private String returnedType;
    private boolean isVoid;

    private String[] paramNames;

    private String methodSignatureWithModifiers;
    private String methodSignatureWithoutModifiers;

    private boolean hasMethodLevelAnnotation;
    private boolean hasClassLevelAnnotation;
    private boolean hasInterfaceMethodLevelAnnotation;
    private boolean hasInterfaceLevelAnnotation;

    public EasyLogSignature(JPSignature signature) {
        if (signature.isAnnotated()) {
            label = signature.effectiveAnnotation().label();
            level = signature.effectiveAnnotation().level();
            ignoreParameters = signature.effectiveAnnotation().ignoreParameters();
            maskFields = signature.effectiveAnnotation().maskFields();
            style = signature.effectiveAnnotation().style();
            retryAttempts = signature.effectiveAnnotation().retryAttempts();
            retryDelay = signature.effectiveAnnotation().retryDelay();
            retryExceptions = signature.effectiveAnnotation().retryExceptions();

            paramNames = signature.paramNames();

            methodSignatureWithModifiers = signature.methodSignatureWithModifiers();
            methodSignatureWithoutModifiers = signature.methodSignatureWithoutModifiers();
            hasMethodLevelAnnotation = signature.hasMethodLevelAnnotation();
            hasClassLevelAnnotation = signature.hasClassLevelAnnotation();
            hasInterfaceMethodLevelAnnotation = signature.hasInterfaceMethodLevelAnnotation();
            hasInterfaceLevelAnnotation = signature.hasInterfaceLevelAnnotation();
            isVoid = signature.isVoid();
        }

    }

    /**
     * This reads names and values of all parameters from
     * ProceedingJoinPoint jp as a map
     */
    public Map<String, Object> getMethodParameters(Object[] values) {
        String[] keys = paramNames;

        Map<String, Object> params = new HashMap<>();
        IntStream.range(0, keys.length).boxed().forEach(i -> {
            if (!isInArray(ignoreParameters, keys[i])) params.put(keys[i], values[i]);
        });
        return params;
    }

    private boolean isInArray(String[] array, String parameterName) {
        return array.length != 0 && Arrays.asList(array).contains(parameterName);
    }

    public boolean isVoid() {
        return isVoid;
    }

    public String methodSignatureWithModifiers() {
        return methodSignatureWithModifiers;
    }

    public String methodSignatureWithoutModifiers() {
        return methodSignatureWithoutModifiers;
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

    public Level level() {
        return level;
    }

    public String label() {
        return label;
    }

    public Style style() {
        return style;
    }

    public String[] maskFields() {
        return maskFields;
    }

    public int retryAttempts() {
        return retryAttempts;
    }

    public long retryDelay() {
        return retryDelay;
    }

    public Class<? extends Throwable>[] retryExceptions() {
        return retryExceptions;
    }
}
