/*
 * Copyright 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to ensure that the parser is working properly for core statements,
 * expressions and edge cases related to core parsing.
 */
public class ParserTest {
    private ScriptEngine salinas;
    private ScriptContext context;

    @BeforeEach
    public void setup() throws Exception {
        salinas = new ScriptEngineManager().getEngineByName("salinas");
        context = salinas.getContext();
    }

    @Test
    public void testUnaryMinusAndParentheses() throws Exception {
        assertEquals(BigDecimal.valueOf(-1),
                salinas.eval("-1-(-(-1))-1+1+1"));
    }

    @Test
    public void testBinaryLiteral() throws Exception {
        assertEquals(BigDecimal.TEN, salinas.eval("0b1010"));
        assertEquals(BigDecimal.TEN, salinas.eval("0b10_10"));
        assertEquals(BigDecimal.valueOf(0b111111), salinas.eval("0b111111"));
    }

    @Test
    public void testHexLiteral() throws Exception {
        assertEquals(BigDecimal.valueOf(0xf), salinas.eval("0xf"));
        assertEquals(BigDecimal.valueOf(0xff), salinas.eval("0xFF"));
        assertEquals(BigDecimal.valueOf(0xffff), salinas.eval("0xFF_FF"));
        assertEquals(BigDecimal.valueOf(0xabcdef), salinas.eval("0xabcdef"));
    }

    @Test
    public void testDecimalLiteralWithUnderscores() throws Exception {
        assertEquals(BigDecimal.valueOf(10), salinas.eval("1_0"));
        assertEquals(BigDecimal.valueOf(10.11), salinas.eval("1_0.1_1"));
        try {
            assertEquals(BigDecimal.valueOf(10.11), salinas.eval("1_0._1_1"));
            fail();
        } catch (ScriptException ex) {
            // Good
        }
        try {
            assertEquals(BigDecimal.valueOf(10.11), salinas.eval("1_0_.1_1"));
            fail("Trailing underscore before decimal is not allowed");
        } catch (ScriptException ex) {
            // Good
        }

        try {
            assertEquals(BigDecimal.valueOf(10.11), salinas.eval("? _1_0.1_1"));
            fail("Leading underscore makes an identifier, and periods can't appear in them");
        } catch (ScriptException ex) {
        }

        try {
            salinas.eval("1_0.1_1_");
            fail("Cannot have trailing underscores in numeric literals");
        } catch (ScriptException ex) {
        }
    }

    @Test
    public void testMultipleMeaningsOfEquals() throws Exception {
        assertTrue((Boolean) salinas.eval("a = (1 = 2 <> 5)\nreturn a"));
    }

    @Test
    public void testContains() throws Exception {
        // Classic contains
        assertTrue((Boolean) salinas.eval("\"a\"$\"ab\""));
        assertFalse((Boolean) salinas.eval("\"a\"$\"cb\""));
        // Contains with multiple search terms (Salinas extension)
        assertTrue((Boolean) salinas.eval("\"c\"$\"a\"$\"abc\""));
        assertFalse((Boolean) salinas.eval("\"d\"$\"e\"$\"abc\""));
    }

    @Test
    public void testIfBlock() throws Exception {
        String result = (String) salinas.eval("a:string = 'if block executed no branches';;if 1 == 1;;a := '1 does equal 1';;elseif 1 == 2;;a := '1 does equal 2';;elseif 3 = 4;;a := '3 does equal 4';;else;;a := 'none are true';;endif;;a");
        assertEquals("1 does equal 1", result);
        result = (String) salinas.eval("a = \"test\";;if a === true;; a = 'true';; endif;; a");
        assertEquals("test", result);
    }

    @Test
    public void testCaseBlock() throws Exception {
        String result = (String) salinas.eval("a = 'case block executed no branches';;do case;;case 1 = 1;;a = 'first';; case 2 = 2;;a = 'second';;otherwise;;a = 'third';;endcase;;a");
        assertEquals("first", result);
        result = (String) salinas.eval("a = 'case block executed no branches';;do case;;case 1 = 2;;a = 'first';; case 2 = 2;;a = 'second';;otherwise;;a = 'third';;endcase;;a");
        assertEquals("second", result);
        result = (String) salinas.eval("a = 'case block executed no branches';;do case;;case 1 = 2;;a = 'first';; case 2 = 3;;a = 'second';;otherwise;;a = 'third';;endcase;;a");
        assertEquals("third", result);
    }

    @Test
    public void testForLoop() throws Exception {
        // Basic for loop
        BigDecimal result = (BigDecimal) salinas.eval("x = 5;; for y = 1 to 10;; x = x + 1;; next;; x");
        assertEquals(BigDecimal.valueOf(15), result);
        // For loop with negative step and modifying index variable
        result = (BigDecimal) salinas.eval("x = -1;;y = 1;;a = 20;;for x = a to 1 step -1;;if y = 1;;x = x + 1;;y = 0;;endif;;next;;x");
        assertEquals(BigDecimal.ONE, result);
    }

    @Test
    public void testWhileLoop() throws Exception {
        BigDecimal result = (BigDecimal) salinas.eval("a = 1;;do while a < 10;;a = a + 1;;enddo;;a");
        assertEquals(BigDecimal.TEN, result);
    }

    @Test
    public void testRootReturnStatement() throws Exception {
        BigDecimal result = (BigDecimal) salinas.eval("a = 1;; return a;; // dead code follows\na = 10;;a");
        assertEquals(BigDecimal.ONE, result);
    }

    @Test
    public void testNamedUserDefinedFunctionAsClosure() throws Exception {
        BigDecimal result = (BigDecimal) salinas.eval("c = 0;;a = function b();;c = c + 1;;endfunc;;a();;b();;c");
        assertEquals(BigDecimal.valueOf(2), result);
    }

    @Test
    public void testReturnFromFunctionAndDefaultParameterValues() throws Exception {
        salinas.eval("a = function c(b = 10);;return b + 1;;endfunc;; a(1);;c(2);;a();;c()");
    }

    @Test
    public void testAccessFunctionBeforeDeclaration() throws Exception {
        assertEquals("called", salinas.eval("a = 'did not call'\n c();;function c();;a = 'called';;endfunc;;a"));
    }

    @Test
    public void testPublicVariable() throws Exception {
        assertEquals("test", salinas.eval("if 1=1;;public a:string = 'test';;endif;;a"));
    }

    @Test
    public void testMultipleBuiltinFunctionsInOneExpression() throws Exception {
        assertEquals("E", salinas.eval("UPPER(SUBSTR('test', 2, 1))"));
    }

    @Test
    public void testMinusSignBeforeFunctionCall() throws Exception {
        assertEquals(BigDecimal.valueOf(-2), salinas.eval("a = function();;return 2;;endfunc;;-a()"));
    }

    @Test
    public void testCallingReturnValueAsFunction() throws Exception {
        assertEquals(BigDecimal.valueOf(3), salinas.eval("function a;;return function(num);;return num + 1;;endfunc;;endfunc;;a()(2)"));
    }

    @Test
    public void testImplicitCreationOfTwoDimensionalArray() throws Exception {
        // Using standard syntax
        assertEquals(BigDecimal.valueOf(5), salinas.eval("a[1][2] = 5;; a[1][2]"));

        // Using legacy syntax
        assertEquals(BigDecimal.valueOf(5), salinas.eval("a[1, 2] = 5;; a[1, 2]"));

        // Two syntaxes are equivalent
        assertEquals(BigDecimal.valueOf(5), salinas.eval("a[1, 2] = 5;; a[1][2]"));
    }

    @Test
    public void testDifferentTypesOfArrayKeyAndValue() throws Exception {
        assertEquals("testing", salinas.eval("public a['test'] = 'testing';; a[2] = 'blah';;a['test']"));
        assertEquals("blah", salinas.eval("public a['test'] = 'testing';; a[2] = 'blah';;a[2]"));
        assertEquals(BigDecimal.TEN, salinas.eval("public a['test'] = 'testing';; a[2] = 10;;a[2]"));
    }

    @Test
    public void testMixedArrayAndDateLiterals() throws Exception {
        salinas.eval("a = {1, {2 / 3}, 3, 'string', {'nested array :)'}, {1/1/01}, {^2001-01-01}}");
    }

    @Test
    public void testSplitLines() throws Exception {
        assertEquals(BigDecimal.ONE, salinas.eval("_t = ;\n1;;_t"));
    }

    @Test
    public void testFunctionNotFound() throws Exception {
        try {
            salinas.eval("nonexistent()");
        } catch (ScriptException ex) {
            assertTrue(ex.getMessage().contains("not found"));
        }
    }
}
