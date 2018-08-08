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
