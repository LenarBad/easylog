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
