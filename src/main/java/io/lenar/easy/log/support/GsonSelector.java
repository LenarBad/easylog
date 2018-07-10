package io.lenar.easy.log.support;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonSelector {

    private static Gson prettyPrintingWithNullsGson;
    private static Gson withNullsGson;
    private static Gson prettyPrintingNoNullsGson;
    private static Gson noNullsGson;

    public static Gson getGson(boolean pretty, boolean nulls) {
        if (pretty) {
            if (nulls) {
                if (prettyPrintingWithNullsGson == null)
                    prettyPrintingWithNullsGson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
                return prettyPrintingWithNullsGson;
            } else {
                if (prettyPrintingNoNullsGson == null)
                    prettyPrintingNoNullsGson = new GsonBuilder().setPrettyPrinting().create();
                return prettyPrintingNoNullsGson;
            }
        } else {
            if (nulls) {
                if (withNullsGson == null)
                    withNullsGson = new GsonBuilder().serializeNulls().create();
                return withNullsGson;
            } else {
                if (noNullsGson == null)
                    noNullsGson = new Gson();
                return noNullsGson;
            }
        }
    }

}
