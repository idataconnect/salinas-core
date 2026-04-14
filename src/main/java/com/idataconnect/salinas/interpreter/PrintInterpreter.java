/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTPRINTLN;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTPRINTPRELN;
import java.io.IOException;


/**
 * An interpreter delegate which prints values to the script context's
 * configured writer.
 */
public class PrintInterpreter implements InterpreterDelegate {

    private static PrintInterpreter instance;

    /**
     * Gets a singleton instance of the print interpreter.
     * @return a singleton instance
     */
    public static PrintInterpreter getInstance() {
        if (instance == null) {
            instance = new PrintInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException {
        try {
            if (node.getId() == JJTPRINTPRELN) {
                context.getWriter().write('\n');
            }

            SalinasValue value = SalinasValue.NULL;
            if (node.jjtGetNumChildren() > 0) {
                value = SalinasInterpreter
                        .interpret((SalinasNode) node.jjtGetChild(0), context);
                context.getWriter().write(value.getDisplayValue(context.getConfig()));
            }

            if (node.getId() == JJTPRINTLN) {
                context.getWriter().write('\n');
            }

            context.getWriter().flush();

            return value;
        } catch (IOException ex) {
            throw new SalinasException("I/O error during output", ex);
        }
    }
}
