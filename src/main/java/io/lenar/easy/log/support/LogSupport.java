package io.lenar.easy.log.support;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class LogSupport {

    private Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private static final String MASKED_VALUE = "XXXMASKEDXXX";

    /**
     * This reads names and values of all parameters from
     * ProceedingJoinPoint jp as a map
     */
    protected Map<String, Object> getMethodParameters(ProceedingJoinPoint jp, String[] ignoreList) {
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
    protected String getMethodSignatureAsString(ProceedingJoinPoint jp, boolean showModifier, String[] ignoreList) {
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

    protected boolean isVoid(ProceedingJoinPoint jp) {
        return getMethodSignature(jp).getReturnType().getSimpleName().equals("void");
    }

    private MethodSignature getMethodSignature(ProceedingJoinPoint jp) {
        return ((MethodSignature) jp.getSignature());
    }

    private boolean isIgnored(String[] ignoreList, String parameterName) {
        return ignoreList.length != 0 && Arrays.asList(ignoreList).contains(parameterName);
    }

    protected String objectToString(Object object, String[] maskFields) {
        if (object == null) return null;
        if (isPrimitiveOrString(object)) return object.toString();
        if (maskFields.length == 0) return objectToString(object);
        if (Collection.class.isAssignableFrom(object.getClass())) return collectionToString(object, maskFields);
        return gson.toJson(getMap(object, maskFields));
    }

    private String collectionToString(Object object, String[] maskFields) {
        Collection collection = (Collection<Object>) object;
        List<Object> list = new ArrayList<>();
        for (Object item : collection) {
            list.add(getMap(item, maskFields));
        }
        return gson.toJson(list);
    }

    protected String objectToString(Object object) {
        return gson.toJson(object);
    }

    private Map<String, Object> getMap(Object object, String[] maskFields) {
        Type itemsMapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> map = gson.fromJson(gson.toJson(object), itemsMapType);
        Map<String, Object> newMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null && Arrays.asList(maskFields).contains(entry.getKey())) {
                // Replace the field that should be masked if not null
                newMap.put(entry.getKey(), MASKED_VALUE);
            } else {
                if (entry.getValue() == null || isPrimitiveOrString(entry.getValue())) {
                    // As is if null/primitive/String
                    newMap.put(entry.getKey(), entry.getValue());
                } else {
                    if (Collection.class.isAssignableFrom(entry.getValue().getClass())) {
                        // As a list of maps if collection
                        List<Object> list = new ArrayList<>();
                        for (Object item : (Collection<Object>) entry.getValue()) {
                            list.add(getMap(item, maskFields));
                        }
                        newMap.put(entry.getKey(), list);
                    } else {
                        newMap.put(entry.getKey(), getMap(entry.getValue(), maskFields));
                    }
                }
            }
        }
        return newMap;
    }

    private Object cloneObject(Object object) {
        return gson.fromJson(gson.toJsonTree(object).deepCopy(), Object.class);
    }

    private boolean isPrimitiveOrString(Object object) {
        return
                object.getClass() == Boolean.class ||
                        object.getClass() == Character.class ||
                        object.getClass() == Byte.class ||
                        object.getClass() == Short.class ||
                        object.getClass() == Integer.class ||
                        object.getClass() == Long.class ||
                        object.getClass() == Float.class ||
                        object.getClass() == Double.class ||
                        object.getClass() == Void.class ||
                        object.getClass() == String.class;
    }

}
