/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasConfig;
import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import java.math.BigDecimal;
import javax.script.ScriptContext;

/**
 * Interpreter delegate for SET statements.
 */
public class SetInterpreter implements InterpreterDelegate {
    
    private static SetInterpreter instance;

    /**
     * Gets a singleton instance of the set interpreter.
     * @return a singleton instance
     */
    public static SetInterpreter getInstance() {
        if (instance == null) {
            instance = new SetInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        SalinasNode identifierNode = node.getChild(0);
        String identifierName = (String) identifierNode.jjtGetValue();
        // TODO delegate to external implementations
        // Also, allow plugins to add identifiers
        if (identifierName.equalsIgnoreCase("decimals")) {
            // Make sure they didn't do SET DECIMALS ON/OFF
            if (node.jjtGetNumChildren() == 1) {
                throw new SalinasException("Invalid usage of SET DECIMALS");
            }

            // Check that decimals are between 0 and 18
            final SalinasValue v = SalinasInterpreter.interpret(
                    node.getChild(1), context);
            final BigDecimal bd = (BigDecimal) v.asType(SalinasType.NUMBER);
            final int decimals = bd.intValue();
            if (decimals < 0 || decimals > 18) {
                throw new SalinasException("Decimals must be between 0 and 18");
            }
            ((SalinasConfig) context.getAttribute("salinasConfig"))
                    .setDecimals(decimals);
            return v;
        }

        return SalinasValue.UNDEFINED;
    }
}
