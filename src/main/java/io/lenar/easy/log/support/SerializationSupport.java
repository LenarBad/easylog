package io.lenar.easy.log.support;

import static io.lenar.easy.log.support.GsonSelector.getGson;

import java.util.Map;

public class SerializationSupport extends Processor {

    public static String objectToString(final Object object, String[] maskFields, boolean pretty, boolean nulls) {
        return getGson(pretty, nulls).toJson(process(object, maskFields));
    }

    public static String paramsToString(final Map<String, Object> params,  String[] maskFields, boolean pretty, boolean nulls) {
        return getGson(pretty, nulls).toJson(process(params, maskFields));
    }

}
