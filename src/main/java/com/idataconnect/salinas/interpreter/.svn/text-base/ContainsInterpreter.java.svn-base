/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import javax.script.ScriptContext;

/**
 * Interpreter delegate implementation for contains expressions.
 */
public class ContainsInterpreter implements InterpreterDelegate {

    private static ContainsInterpreter instance;

    /**
     * Gets a singleton instance of the contains interpreter.
     * @return a singleton instance
     */
    public static ContainsInterpreter getInstance() {
        if (instance == null) {
            instance = new ContainsInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        final String haystack = (String) SalinasInterpreter.interpret(
                (SalinasNode) node.jjtGetChild(node.jjtGetNumChildren() - 1),
                context).asType(SalinasType.STRING);
        for (int count = 0; count < node.jjtGetNumChildren() - 1; count++) {
            final String needle = (String) SalinasInterpreter.interpret(
                (SalinasNode) node.jjtGetChild(count),
                context).asType(SalinasType.STRING);
            if (haystack.contains(needle)) {
                return SalinasValue.TRUE;
            }
        }

        return SalinasValue.FALSE;
    }
}
