/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;

/**
 * Interpreter delegate interface.
 */
public interface InterpreterDelegate {

    /**
     * interpret the given node using state from the given execution context.
     * @param node the AST node to be interpreted
     * @param context the execution context
     * @return the value as a result of interpreting the node
     * @throws SalinasException if a runtime error occurs
     */
    SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException;
}
