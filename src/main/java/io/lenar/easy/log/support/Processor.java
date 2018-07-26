package io.lenar.easy.log.support;

import static io.lenar.easy.log.support.Processor.ObjectType.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Processor {

    public static final String MASKED_VALUE = "XXXMASKEDXXX";
    private static final boolean DEBUG = false;

    public static Object process(Object object, String[] maskFields) {
        return processObject(object, maskFields);
    }

    private static Object processObject(Object object, String[] maskFields) {
        if (object == null) return null;
        ObjectType type = getType(object);
        debug("  TYPE: " + type.name() + " " + object.toString());
        switch (type) {
            case STRING:
            case PRIMITIVE:
            case DATE:
            case ENUM: return object;
            case MAP: return processMap((Map<String, Object>) object, maskFields);
            case OBJECT: return processMap(objectAsMap(object), maskFields);
            case COLLECTION: return processCollection(object, maskFields);
            case ARRAY: return processCollection(Arrays.asList((Object[]) object), maskFields);
        }
        debug("!!! LOGGING: Couldn't serialize - not supported Type !!!");
        return object;
    }

    private static List<Object> processCollection(Object object, String[] maskFields) {
        Collection collection = (Collection<Object>) object;
        List<Object> list = new ArrayList<>();
        for (Object item : collection) {
            list.add(processObject(item, maskFields));
        }
        return list;
    }

    private static Map<String, Object> objectAsMap(Object obj) {
        Class<? extends Object> clazz = obj.getClass();
        Map<String, Object> map = new HashMap<>();
        map = objectAsMapWithParents(clazz, obj, map);
        return map;
    }

    private static Map<String, Object> objectAsMapWithParents(Class clazz, Object obj, Map<String, Object> map) {
        try {
            Field[] fields = clazz.getDeclaredFields();
            debug("Class: " + clazz.getSimpleName());
            for (int i = 0; i < fields.length; i++) {
                if (!Modifier.isProtected(fields[i].getModifiers())) {
                    String name = fields[i].getName();
                    if (!map.containsKey(name)) {
                        fields[i].setAccessible(true);
                        Object value = fields[i].get(obj);
                        map.put(name, value);
                    }
                }
            }
            Class superClazz = clazz.getSuperclass();
            if (superClazz != null) {
                map = objectAsMapWithParents(superClazz, obj, map);
            }
        } catch (Exception ex) {
            map.put("LOGGING_ERROR", "Failed to log object");
        }
        return map;
    }

    private static Object processMap(Map<String, Object> map, String[] maskFields) {
        Map<String, Object> newMap = new HashMap<>();
        debug("\nPROCESSING MAP: " + map.toString());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            debug("  key:" + entry.getKey() + " value: " + entry.getValue());
            if (!needToMask(entry.getKey(), maskFields)) {
                newMap.put(entry.getKey(), processObject(entry.getValue(), maskFields));
            } else {
                if (entry.getValue() == null) {
                    newMap.put(entry.getKey(), null);
                } else {
                    newMap.put(entry.getKey(), MASKED_VALUE);
                }
            }
        }
        return newMap;
    }

    public static boolean needToMask(String name, String[] maskFields) {
        return Arrays.asList(maskFields).contains(name);
    }

    private static ObjectType getType(Object object) {
        if (object == null) {
            return NULL;
        }
        Class clazz = object.getClass();

        if (clazz == Boolean.class || clazz == Character.class || clazz == Byte.class || clazz == Short.class ||
                clazz == Integer.class || clazz == Long.class || clazz == Float.class || clazz == Double.class ||
                clazz == Void.class) {
            return PRIMITIVE;
        }
        if (clazz == String.class) {
            return STRING;
        }
        if (Collection.class.isAssignableFrom(clazz)) {
            return COLLECTION;
        }
        if (clazz.isArray()) {
            return ARRAY;
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return MAP;
        }
        if (object instanceof Enum) {
            return ENUM;
        }
        if (object instanceof Date || object instanceof LocalDate) {
            return DATE;
        }
        return OBJECT;
    }

    protected enum ObjectType {
        PRIMITIVE, STRING, COLLECTION, ARRAY, MAP, ENUM, NULL, OBJECT, DATE;
    }

    private static void debug(String message) {
        if (DEBUG) System.out.println(message);
    }

}
