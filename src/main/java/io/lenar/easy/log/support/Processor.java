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
package io.lenar.easy.log.support;

import static io.lenar.easy.log.support.Processor.ObjectType.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
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
            case MAP: return processMap(object, maskFields);
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
                if (!Modifier.isProtected(fields[i].getModifiers()) && !Modifier.isStatic(fields[i].getModifiers())) {
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

    private static Object processMap(Object object, String[] maskFields) {
        Map<Object, Object> oldMap = (Map<Object, Object>) object;
        Map<String, Object> newMap = new HashMap<>();
        debug("\nPROCESSING MAP: " + oldMap.toString());
        for (Map.Entry<Object, Object> entry : oldMap.entrySet()) {
            debug("  key:" + entry.getKey() + " value: " + entry.getValue());
            if (!needToMask(entry.getKey().toString(), maskFields)) {
                newMap.put(entry.getKey().toString(), processObject(entry.getValue(), maskFields));
            } else {
                if (entry.getValue() == null) {
                    newMap.put(entry.getKey().toString(), null);
                } else {
                    newMap.put(entry.getKey().toString(), MASKED_VALUE);
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
                clazz == BigInteger.class || clazz == BigDecimal.class ||
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
