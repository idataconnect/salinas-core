/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import javax.script.ScriptContext;

/**
 * Interpreter delegate implementation for statement nodes.
 */
public class StatementInterpreter implements InterpreterDelegate {

    private static StatementInterpreter instance;

    /**
     * Gets a singleton instance of the statement interpreter.
     * @return a singleton instance
     */
    public static StatementInterpreter getInstance() {
        if (instance == null) {
            instance = new StatementInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        if (node.jjtGetNumChildren() == 0) {
            return null;
        } else {
            SalinasValue value = null;
            for (int count = 0; count < node.jjtGetNumChildren(); count++) {
                SalinasNode child = (SalinasNode) node.jjtGetChild(count);
                value = SalinasInterpreter.interpret(child, context);
            }

            return value;
        }
    }
}
