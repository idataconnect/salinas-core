/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import javax.script.ScriptContext;

/**
 * Interpreter delegate implementation for RETURN statement nodes.
 */
public class ReturnInterpreter implements InterpreterDelegate {

    private static ReturnInterpreter instance;

    /**
     * Gets a singleton instance of the return interpreter.
     * @return a singleton instance
     */
    public static ReturnInterpreter getInstance() {
        if (instance == null) {
            instance = new ReturnInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        final SalinasNode expressionNode = (SalinasNode) node.jjtGetChild(0);
        final SalinasValue value = SalinasInterpreter
                .interpret(expressionNode, context);
        context.setAttribute("returning", value, ScriptContext.ENGINE_SCOPE);
        return value;
    }
}
