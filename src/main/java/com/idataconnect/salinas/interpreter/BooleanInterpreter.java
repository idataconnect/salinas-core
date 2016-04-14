/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import javax.script.ScriptContext;

/**
 * An interpreter delegate for boolean nodes.
 */
public class BooleanInterpreter implements InterpreterDelegate {

    private static BooleanInterpreter instance;

    /**
     * Gets a singleton instance of the boolean interpreter.
     * @return a singleton instance
     */
    public static BooleanInterpreter getInstance() {
        if (instance == null) {
            instance = new BooleanInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        if (node.getId() == JJTBOOLEANNOT) {
            return SalinasInterpreter.interpret((SalinasNode) node.jjtGetChild(0), context)
                    .asType(SalinasType.BOOLEAN).equals(Boolean.TRUE)
                    ? SalinasValue.FALSE : SalinasValue.TRUE;
        }
        
        SalinasValue currentValue = null;
        for (int count = 0; count < node.jjtGetNumChildren(); count++) {
            SalinasNode operandNode = (SalinasNode) node.jjtGetChild(count);
            currentValue = SalinasInterpreter
                    .interpret(operandNode, context);
            final boolean booleanValue = (Boolean) currentValue
                    .asType(SalinasType.BOOLEAN);
            switch (node.getId()) {
                case JJTAND:
                    if (!booleanValue) {
                        return SalinasValue.FALSE;
                    }
                    break;
                case JJTOR:
                    if (booleanValue) {
                        return SalinasValue.TRUE;
                    }
                    break;
            }
        }

        return node.getId() == JJTAND
                ? SalinasValue.TRUE
                : SalinasValue.FALSE;
    }
}
