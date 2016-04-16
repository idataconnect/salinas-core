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
 * Tests for internal functions.
 */
public class InternalFunctionTest {
    
    private ScriptEngine salinas;
    private ScriptContext context;

    @Before
    public void setup() throws Exception {
        salinas = new ScriptEngineManager().getEngineByName("salinas");
        context = salinas.getContext();
    }

    @Test
    public void testPi() throws Exception {
        BigDecimal pi = (BigDecimal) salinas.eval("PI()");
        assertEquals(pi, BigDecimal.valueOf(Math.PI));
    }

    @Test
    public void testSubstr() throws Exception {
        String abc = (String) salinas.eval("SUBSTR('ABCDE', 1, 3)");
        assertEquals("ABC", abc);
        String cd = (String) salinas.eval("SUBSTR('ABCDE', 3, 2)");
        assertEquals("CD", cd);
    }
    
    @Test
    public void testRandom() throws Exception {
        ScriptEngine e1 = new ScriptEngineManager().getEngineByName("salinas");
        ScriptEngine e2 = new ScriptEngineManager().getEngineByName("salinas");

        BigDecimal number1 = (BigDecimal) e1.eval("RANDOM()");
        double d = number1.doubleValue();
        // Check that the random result is within bounds
        assertTrue(d >= 0 && d < 1);

        // Different engines should use different Random instances with the
        // same initial seed
        BigDecimal number2 = (BigDecimal) e2.eval("RANDOM()");
        assertEquals(number1, number2);

        // The same positive seed should return the same result, in
        // two different engines.
        assertEquals(e1.eval("RANDOM(1000)"), e2.eval("RANDOM(1000)"));

        // Same for subsequent calls
        assertEquals(e1.eval("RANDOM(1000)"), e2.eval("RANDOM(1000)"));

        // Same seed in the same engine should return different results, since
        // pulling a result changes the sequence, and the same seed won't
        // reset the sequence.
        assertFalse(e1.eval("RANDOM(1000)").equals(e1.eval("RANDOM(1000)")));

        // Different results for different positive seeds
        assertFalse(e1.eval("RANDOM(1000)").equals(e2.eval("RANDOM(1001)")));

        // Different results for the same negative seed (randomized seed)
        assertFalse(e1.eval("RANDOM(-100)").equals(e1.eval("RANDOM(-100)")));
        assertFalse(e1.eval("RANDOM(-100)").equals(e2.eval("RANDOM(-100)")));
        assertFalse(e1.eval("RANDOM(-1)").equals(e1.eval("RANDOM(-1)")));
        assertFalse(e1.eval("RANDOM(-1)").equals(e2.eval("RANDOM(-1)")));

    }

    @Test
    public void testRound() throws Exception {
        BigDecimal number = (BigDecimal) salinas.eval("ROUND(20.1)");
        assertEquals(BigDecimal.valueOf(20).stripTrailingZeros(), number.stripTrailingZeros());
        number = (BigDecimal) salinas.eval("ROUND(20.1, 2)");
        assertEquals(BigDecimal.valueOf(20.1).stripTrailingZeros(), number.stripTrailingZeros());
        number = (BigDecimal) salinas.eval("ROUND(20.1, 1)");
        assertEquals(BigDecimal.valueOf(20.1).stripTrailingZeros(), number.stripTrailingZeros());
        number = (BigDecimal) salinas.eval("ROUND(20.1234, 3)");
        assertEquals(BigDecimal.valueOf(20.123).stripTrailingZeros(), number.stripTrailingZeros());
    }

    @Test
    public void testLeft() throws Exception {
        // Part of the string
        assertEquals("TE", salinas.eval("LEFT('TEST', 2)"));
        
        // All of the string
        assertEquals("TEST", salinas.eval("LEFT('TEST', 4)"));
        
        // More than the length of the string, returns the whole string
        assertEquals("TEST", salinas.eval("LEFT('TEST', 6)"));
    }

    @Test
    public void testRight() throws Exception {
        // Part of the string
        assertEquals("ST", salinas.eval("RIGHT('TEST', 2)"));
        
        // All of the string
        assertEquals("TEST", salinas.eval("RIGHT('TEST', 4)"));
        
        // More than the length of the string, returns the whole string
        assertEquals("TEST", salinas.eval("RIGHT('TEST', 6)"));
    }

    @Test
    public void testUpper() throws Exception {
        assertEquals("TEST", salinas.eval("UPPER('test')"));
        assertEquals("TEST", salinas.eval("UPPER('TeSt')"));
        assertEquals("TEST", salinas.eval("UPPER('TEST')"));
    }

    @Test
    public void testLower() throws Exception {
        assertEquals("test", salinas.eval("LOWER('test')"));
        assertEquals("test", salinas.eval("LOWER('TeSt')"));
        assertEquals("test", salinas.eval("LOWER('TEST')"));
    }

    @Test
    public void testRat() throws Exception {
        assertEquals(BigDecimal.ONE, salinas.eval("RAT('A', 'ABC')"));
        assertEquals(BigDecimal.valueOf(3), salinas.eval("RAT('A', 'ABA')"));
        assertEquals(BigDecimal.valueOf(2), salinas.eval("RAT('BC', 'ABCA')"));
        assertEquals(BigDecimal.valueOf(2), salinas.eval("RAT('BC', 'ABCABCA', 2)"));
        assertEquals(BigDecimal.ZERO, salinas.eval("RAT('BC', 'ABCABCA', 3)"));
        assertEquals(BigDecimal.valueOf(5), salinas.eval("RAT('BC', 'ABCABCA')"));
    }

    @Test
    public void testAt() throws Exception {
        assertEquals(BigDecimal.ONE, salinas.eval("AT('A', 'ABC')"));
        assertEquals(BigDecimal.valueOf(1), salinas.eval("AT('A', 'ABA')"));
        assertEquals(BigDecimal.valueOf(2), salinas.eval("AT('BC', 'ABCA')"));
        assertEquals(BigDecimal.valueOf(5), salinas.eval("AT('BC', 'ABCABCA', 2)"));
        assertEquals(BigDecimal.ZERO, salinas.eval("AT('BC', 'ABCABCA', 3)"));
        assertEquals(BigDecimal.valueOf(2), salinas.eval("AT('BC', 'ABCABCA')"));
    }

    @Test
    public void testAsc() throws Exception {
        assertEquals(BigDecimal.valueOf(32), salinas.eval("ASC(' ')"));
        assertEquals(BigDecimal.valueOf(65), salinas.eval("ASC('A')"));
    }

    @Test
    public void testChr() throws Exception {
        assertEquals(" ", salinas.eval("CHR(32)"));
        assertEquals("A", salinas.eval("CHR(65)"));
    }

    @Test
    public void testIif() throws Exception {
        assertEquals(BigDecimal.TEN, salinas.eval("IIF(true, 10, 1)"));
        assertEquals(BigDecimal.ONE, salinas.eval("IIF(false, 10, 1)"));
    }

    @Test
    public void testCenter() throws Exception {
        assertEquals("*a**", salinas.eval("CENTER('a', 4, '*')"));
    }
}
