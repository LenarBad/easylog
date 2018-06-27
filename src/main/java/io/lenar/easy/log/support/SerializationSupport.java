package io.lenar.easy.log.support;

import static io.lenar.easy.log.support.SerializationSupport.ObjectType.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class SerializationSupport {

    private static Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private static final String MASKED_VALUE = "XXXMASKEDXXX";

    public static String objectToString(Object object, String[] maskFields) {
        if (object == null) return null;
        if (maskFields.length == 0) return objectToString(object);
        if (isPrimitiveOrString(object)) return object.toString();
        ObjectType type = getType(object);
        switch (type) {
            case STRING:
            case PRIMITIVE:
            case ENUM:
                return object.toString();
            case MAP:
            case OBJECT: return objectToString(getMap(object, maskFields));
            case COLLECTION: return collectionToString(object, maskFields);
            case ARRAY: return collectionToString(Arrays.asList((Object[]) object), maskFields);
        }
        return "!!! LOGGING: Couldn't serialize - not supported Type !!!";
    }

    public static String objectToString(Object object) {
        return gson.toJson(object);
    }

    private static String collectionToString(Object object, String[] maskFields) {
        Collection collection = (Collection<Object>) object;
        List<Object> list = new ArrayList<>();
        for (Object item : collection) {
            list.add(getMap(item, maskFields));
        }
        return gson.toJson(list);
    }

    private static Map<String, Object> getMap(Object object, String[] maskFields) {
        Type itemsMapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> map = gson.fromJson(gson.toJson(object), itemsMapType);
        Map<String, Object> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {

            if (!needToMask(entry.getKey(), maskFields)) {
                ObjectType type = getType(entry.getValue());
                switch (type) {
                    case STRING:
                    case PRIMITIVE:
                        // As is if primitive/String
                        newMap.put(entry.getKey(), entry.getValue());
                        break;
                    case ENUM:
                    case MAP:
                    case OBJECT:
                        newMap.put(entry.getKey(), getMap(entry.getValue(), maskFields));
                        break;
                    case COLLECTION: {
                        List<Object> list = new ArrayList<>();
                        for (Object item : (Collection<Object>) entry.getValue()) {
                            list.add(getMap(item, maskFields));
                        }
                        newMap.put(entry.getKey(), list);
                        break;
                    }
                    case ARRAY: {
                        List<Object> list = new ArrayList<>();
                        for (Object item : (Object[]) entry.getValue()) {
                            list.add(getMap(item, maskFields));
                        }
                        newMap.put(entry.getKey(), list);
                        break;
                    }
                    case NULL:
                        newMap.put(entry.getKey(), null);
                        break;
                    default: newMap.put(entry.getKey(), "!!! LOGGING: Couldn't serialize - not supported Type !!!");
                }
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

    private static Object cloneObject(Object object) {
        return gson.fromJson(gson.toJsonTree(object).deepCopy(), Object.class);
    }

    private static boolean isPrimitiveOrString(Object object) {
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
        return OBJECT;
    }

    enum ObjectType {
        PRIMITIVE, STRING, COLLECTION, ARRAY, MAP, ENUM, NULL, OBJECT;
    }

}
