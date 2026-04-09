/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import com.idataconnect.salinas.parser.SalinasParserTreeConstants;
import java.math.BigDecimal;
import java.io.File;

/**
 * Interpreter delegate for SET statements and expressions.
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
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException {

        // Handle SET("setting") expression
        if (node.getId() == SalinasParserTreeConstants.JJTSETEXPRESSION) {
            SalinasValue v = SalinasInterpreter.interpret(node.getChild(0), context);
            String setting = ((String) v.asType(SalinasType.STRING)).toUpperCase();
            if (setting.equals("DIRECTORY") || setting.equals("DEFAULT")) {
                File dir = context.getConfig().getCurrentDirectory();
                return new SalinasValue(dir != null ? dir.getAbsolutePath() : "", SalinasType.STRING);
            } else if (setting.equals("DECIMALS")) {
                return new SalinasValue(BigDecimal.valueOf(context.getConfig().getDecimals()), SalinasType.NUMBER);
            } else if (setting.equals("PRECISION")) {
                return new SalinasValue(BigDecimal.valueOf(context.getConfig().getPrecision()), SalinasType.NUMBER);
            }
            return SalinasValue.NULL;
        }

        SalinasNode identifierNode = node.getChild(0);
        String identifierName = (String) identifierNode.jjtGetValue();
        
        // Handle SET DECIMALS TO <num>
        if (identifierName.equalsIgnoreCase("decimals")) {
            if (node.jjtGetNumChildren() == 1) {
                throw new SalinasException("Invalid usage of SET DECIMALS");
            }

            final SalinasValue v = SalinasInterpreter.interpret(
                    node.getChild(1), context);
            final int decimals = ((BigDecimal) v.asType(SalinasType.NUMBER)).intValue();
            if (decimals < 0 || decimals > 18) {
                throw new SalinasException("Decimals must be between 0 and 18");
            }
            context.getConfig().setDecimals(decimals);
            return v;
        }

        // Handle SET PRECISION TO <num>
        if (identifierName.equalsIgnoreCase("precision")) {
            if (node.jjtGetNumChildren() == 1) {
                throw new SalinasException("Invalid usage of SET PRECISION");
            }

            final SalinasValue v = SalinasInterpreter.interpret(
                    node.getChild(1), context);
            final int precision = ((BigDecimal) v.asType(SalinasType.NUMBER)).intValue();
            context.getConfig().setPrecision(precision);
            return v;
        }

        // Handle SET DIRECTORY TO <path> or SET DEFAULT TO <path>
        if (identifierName.equalsIgnoreCase("directory") || identifierName.equalsIgnoreCase("default")) {
            if (node.jjtGetNumChildren() == 1) {
                context.getConfig().setCurrentDirectory(null);
                return SalinasValue.NULL;
            }

            final SalinasValue v = SalinasInterpreter.interpret(
                    node.getChild(1), context);
            final String path = (String) v.asType(SalinasType.STRING);
            context.getConfig().setCurrentDirectory(new File(path));
            return v;
        }

        return SalinasValue.UNDEFINED;
    }
}
