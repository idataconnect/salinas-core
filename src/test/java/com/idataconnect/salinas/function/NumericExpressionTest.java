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
 * Unit tests for numeric expressions.
 */
public class NumericExpressionTest {

    private ScriptEngine scriptEngine;
    private ScriptContext context;

    @Before
    public void setup() throws Exception {
        scriptEngine = new ScriptEngineManager().getEngineByName("salinas");
        context = scriptEngine.getContext();
    }

    @Test
    public void testAddition() throws Exception {
        assertEquals(BigDecimal.TEN, scriptEngine.eval("5+5"));
    }

    @Test
    public void testSubtraction() throws Exception {
        assertEquals(BigDecimal.TEN, scriptEngine.eval("20-10"));
    }

    @Test
    public void testMod() throws Exception {
        assertEquals(BigDecimal.TEN, scriptEngine.eval("30%20"));
    }

    @Test
    public void testExp() throws Exception {
        assertEquals(BigDecimal.valueOf(64), scriptEngine.eval("8**2"));
        assertEquals(BigDecimal.valueOf(64), scriptEngine.eval("8^2"));
        assertEquals(BigDecimal.valueOf(3125), scriptEngine.eval("5^5"));
    }

    @Test
    public void testPrecedence() throws Exception {
        // A precedence example from the official xBase dBL reference guide
        assertEquals(Boolean.FALSE, scriptEngine.eval("4+5*(6+2*(8-4)-9)%19>=11"));
    }
    
    @Test
    public void testFloatingPointAndBasicMath() throws Exception {
        assertEquals(new BigDecimal("33.375"),
                ((BigDecimal) scriptEngine.eval("6.25 * 3 / 2 + 4 * 6"))
                .stripTrailingZeros());
    }

    @Test
    public void testUnaryMinusAndFloatingPoint() throws Exception {
        assertEquals(new BigDecimal("-15.75"),
                ((BigDecimal) scriptEngine.eval("-5.25 * 3"))
                .stripTrailingZeros());
    }

    @Test
    public void testVariablesAndArithmetic() throws Exception {
        assertEquals(BigDecimal.valueOf(-7), scriptEngine.eval("a=1\n-5 + -((3)) + a"));
        assertEquals(BigDecimal.valueOf(3), scriptEngine.eval("a=1\na\na * 2 + 1"));
        assertEquals(BigDecimal.valueOf(50), scriptEngine.eval("x = 5;;y = 8;;x * (y + 2)"));
    }
}
