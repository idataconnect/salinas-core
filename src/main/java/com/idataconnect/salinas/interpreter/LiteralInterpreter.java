/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.script.ScriptContext;

/**
 * Interpreter delegate implementation for nodes containing literal values.
 */
public class LiteralInterpreter implements InterpreterDelegate {

    private static LiteralInterpreter instance;

    /**
     * Gets a singleton instance of the literal interpreter.
     * @return a singleton instance
     */
    public static LiteralInterpreter getInstance() {
        if (instance == null) {
            instance = new LiteralInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context) {
        switch (node.getId()) {
            case JJTSTRING:
                return new SalinasValue(node.jjtGetValue().toString()
                        .substring(1, node.jjtGetValue().toString().length() - 1),
                        SalinasType.STRING);
            case JJTNUMBER:
                final String stringValue = ((String) node.jjtGetValue()).replace("_", "");
                if (stringValue.length() > 2 && stringValue.charAt(0) == '0'
                        && (stringValue.charAt(1) == 'x'
                        || stringValue.charAt(1) == 'X')) {
                    // Hex literal
                    final BigInteger bi = new BigInteger(stringValue.substring(2), 16);
                    return new SalinasValue(new BigDecimal(bi), SalinasType.NUMBER);
                } else if (stringValue.length() > 2 && stringValue.charAt(0) == '0'
                        && (stringValue.charAt(1) == 'b'
                        || stringValue.charAt(1) == 'B')) {
                    // Binary literal
                    final BigInteger bi = new BigInteger(stringValue.substring(2), 2);
                    return new SalinasValue(new BigDecimal(bi), SalinasType.NUMBER);
                } else {
                    // Number literal
                    return new SalinasValue(
                            new BigDecimal(stringValue), SalinasType.NUMBER);
                }
            case JJTBOOLEAN:
                return new SalinasValue(node.jjtGetValue(),
                        SalinasType.BOOLEAN);
            default:
                return null;
        }
    }
}
