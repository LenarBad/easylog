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

import io.lenar.easy.log.support.testclasses.WithStaticFields;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class ProcessorTest {

    @Test
    public void mapLongObjectWithMaskFields() {
        Map<Long, String> testMap = new HashMap<>();
        testMap.put(new Long(1), "One");
        testMap.put(new Long(2), "Two");
        String[] maskFields = {"2"};
        Map<String, String> resultMap = (Map<String, String>) Processor.process(testMap, maskFields);

        assertEquals(resultMap.size(), 2, "Expected to items in a Map");
        assertEquals(resultMap.get("2"), Processor.MASKED_VALUE, "Couldn't process Map<Long, String> and mask a field");
    }

    @Test
    public void mapDoubleObjectWithMaskFields() {
        Map<Double, String> testMap = new HashMap<>();
        testMap.put(new Double(1.1), "One point one");
        testMap.put(new Double(2.2), "Two point two");
        String[] maskFields = {"2.2"};

        Map<String, String> resultMap = (Map<String, String>) Processor.process(testMap, maskFields);

        assertEquals(resultMap.size(), 2, "Expected to items in a Map");
        assertEquals(resultMap.get("2.2"), Processor.MASKED_VALUE, "Couldn't process Map<Long, String> and mask a field");
    }

    @Test
    public void dontLogStaticField() {
        WithStaticFields testObject = new WithStaticFields();
        String[] maskFields = {"password"};
        Map<String, String> resultMap = (Map<String, String>) Processor.process(testObject, maskFields);

        assertFalse(resultMap.containsKey("staticField"), "staticFields has been logged");
        assertEquals(resultMap.get("password"), Processor.MASKED_VALUE, "Field <password> should be masked");
    }

}
