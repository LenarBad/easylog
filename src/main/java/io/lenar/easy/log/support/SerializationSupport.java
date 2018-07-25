package io.lenar.easy.log.support;

import static io.lenar.easy.log.support.GsonSelector.getGson;

import java.util.Map;

public class SerializationSupport extends Processor {

    public static String objectToString(final Object object, String[] maskFields, boolean pretty, boolean nulls) {
        return getGson(pretty, nulls).toJson(process(object, maskFields));
    }

    // TODO Re-work that. I don't like to have "processing" logic here
    public static String paramsToString(final Map<String, Object> params,  String[] maskFields, boolean pretty, boolean nulls) {
        String message = "";
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            message = message+ entry.getKey() + ": ";
            if (entry.getValue() == null) {
                message = message + "null\n";
            } else {
                if (!needToMask(entry.getKey(), maskFields)) {
                    message = message + getGson(pretty, nulls).toJson(process(entry.getValue(), maskFields)) + "\n";
                } else {
                    message = message + MASKED_VALUE + "\n";
                }
            }
        }

        return message;
    }

}
