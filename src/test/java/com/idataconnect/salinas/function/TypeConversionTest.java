/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.function;

import java.math.BigDecimal;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for type conversion.
 */
public class TypeConversionTest {

    private ScriptEngine scriptEngine;
    private ScriptContext context;

    @Before
    public void setup() throws Exception {
        scriptEngine = new ScriptEngineManager().getEngineByName("salinas");
        context = scriptEngine.getContext();
    }

    @Test
    public void testConversionBetweenTrueAndOne() throws Exception {
        // All values are concatenated since one of the parameters is a string
        assertEquals("111", scriptEngine.eval("1+.t.+\"1\""));
    }

    @Test
    public void testStrongType() throws Exception {
        assertEquals(BigDecimal.valueOf(410),
                scriptEngine.eval("x = 5;;y:string = 8;;x * (y + 2)"));
    }

    @Test
    public void testEqualityOperatorWithTypeConversion() throws Exception {
        assertTrue((Boolean) scriptEngine.eval("3 == \"3\""));
    }

    @Test
    public void testLateApplicationOfStrongType() throws Exception {
        assertFalse((Boolean) scriptEngine.eval(
                "a = false;; a = a + 1;; a:boolean;; ? a;; a = -a+1;;a"));
        assertTrue((Boolean) scriptEngine.eval(
                "a = false;; a = a + 2;; a:boolean"));
        assertFalse((Boolean) scriptEngine.eval(
                "a = false;; a = a + 2;; a:boolean;; a = a - 1"));
    }
}
