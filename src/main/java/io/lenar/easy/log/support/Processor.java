package io.lenar.easy.log.support;

import static io.lenar.easy.log.support.Processor.ObjectType.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Processor {

    private static final String MASKED_VALUE = "XXXMASKEDXXX";
    private static final boolean DEBUG = false;

    public static Object process(Object object, String[] maskFields, boolean nulls) {
        return processObject(object, maskFields, nulls);
    }

    private static Object processObject(Object object, String[] maskFields, boolean nulls) {
        if (object == null) return null;
        ObjectType type = getType(object);
        debug("  TYPE: " + type.name() + " " + object.toString());
        switch (type) {
            case STRING:
            case PRIMITIVE:
            case DATE:
            case ENUM: return object;
            case MAP: return processMap((Map<String, Object>) object, maskFields, nulls);
            case OBJECT: return processMap(objectAsMap(object), maskFields, nulls);
            case COLLECTION: return processCollection(object, maskFields, nulls);
            case ARRAY: return processCollection(Arrays.asList((Object[]) object), maskFields, nulls);
        }
        debug("!!! LOGGING: Couldn't serialize - not supported Type !!!");
        return object;
    }

    private static List<Object> processCollection(Object object, String[] maskFields, boolean nulls) {
        Collection collection = (Collection<Object>) object;
        List<Object> list = new ArrayList<>();
        for (Object item : collection) {
            list.add(processObject(item, maskFields, nulls));
        }
        return list;
    }

    private static Map<String, Object> objectAsMap(Object obj)
    {
        Class<? extends Object> c1 = obj.getClass();
        Map<String, Object> map = new HashMap<>();
        try {
            Field[] fields = c1.getDeclaredFields();
            debug("OBJECT TO MAP: " + obj.toString());
            for (int i = 0; i < fields.length; i++) {
                String name = fields[i].getName();
                fields[i].setAccessible(true);
                Object value = fields[i].get(obj);
                map.put(name, value);
            }
        } catch (Exception ex) {
            map.put("LOGGING_ERROR", "Failed to log object");
        }
        return map;
    }

    private static Object processMap(Map<String, Object> map, String[] maskFields, boolean nulls) {
        Map<String, Object> newMap = new HashMap<>();
        debug("\nPROCESSING MAP: " + map.toString());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            debug("  key:" + entry.getKey() + " value: " + entry.getValue());
            if (!needToMask(entry.getKey(), maskFields)) {
                newMap.put(entry.getKey(), processObject(entry.getValue(), maskFields, nulls));
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

    private static boolean needToMask(String name, String[] maskFields) {
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
        if (object instanceof Date) {
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
