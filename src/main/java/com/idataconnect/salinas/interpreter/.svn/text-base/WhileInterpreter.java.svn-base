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
 * Interpreter delegate implementation for WHILE loop nodes.
 */
public class WhileInterpreter implements InterpreterDelegate {

    private static WhileInterpreter instance;

    /**
     * Gets a singleton instance of the WHILE loop interpreter.
     * @return a singleton instance
     */
    public static WhileInterpreter getInstance() {
        if (instance == null) {
            instance = new WhileInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        final SalinasNode expressionNode = (SalinasNode) node.jjtGetChild(0);
        SalinasValue returnValue = SalinasValue.NULL;
        while (SalinasInterpreter.interpret(expressionNode, context).asType(
                SalinasType.BOOLEAN).equals(Boolean.TRUE)) {
            for (int count = 1; count < node.jjtGetNumChildren(); count++) {
                returnValue = SalinasInterpreter.interpret(
                        (SalinasNode) node.jjtGetChild(count), context);

                final SalinasValue returning = (SalinasValue) context.getAttribute("returning",
                        ScriptContext.ENGINE_SCOPE);
                if (returning != null) {
                    return returning;
                }
            }
        }

        return returnValue;
    }
}
