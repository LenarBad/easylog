
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

import static io.lenar.easy.log.support.GsonSelector.getGson;

import java.util.Map;

public class SerializationSupport extends Processor {

    public static String objectToString(final Object object, String[] maskFields, boolean pretty, boolean nulls) {
        return getGson(pretty, nulls).toJson(process(object, maskFields));
    }

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
