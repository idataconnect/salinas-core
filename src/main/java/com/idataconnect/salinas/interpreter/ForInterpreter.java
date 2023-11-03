/*
 * Copyright 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.ComparativeOp;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTIDENTIFIER;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTSTEP;
import java.math.BigDecimal;
import javax.script.ScriptContext;

/**
 * Interpreter delegate implementation for FOR loop nodes.
 */
public class ForInterpreter implements InterpreterDelegate {

    private static ForInterpreter instance;

    /**
     * Gets a singleton instance of the FOR interpreter delegate.
     * @return a singleton instance
     */
    public static ForInterpreter getInstance() {
        if (instance == null) {
            instance = new ForInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        assert ((SalinasNode) node.jjtGetChild(0)).getId() == JJTIDENTIFIER;
        SalinasNode identifierNode = (SalinasNode) node.jjtGetChild(0);
        final String identifierName = (String) identifierNode.jjtGetValue();
        SalinasValue indexValue = node.getVariable(identifierName, context).orElseGet(() -> {
            SalinasValue v = new SalinasValue(BigDecimal.ZERO, SalinasType.NUMBER, true);
            node.setVariable(identifierName, v, context);
            return v;
        });

        SalinasValue step = new SalinasValue(BigDecimal.ONE, SalinasType.NUMBER, true);
        indexValue.setValue(SalinasInterpreter.interpret((SalinasNode) node.jjtGetChild(1),
                context).asType(SalinasType.NUMBER));
        SalinasValue stopValue = SalinasInterpreter.interpret((SalinasNode)
                node.jjtGetChild(2), context);
        
        int startIndex = 3;
        SalinasNode possibleStepNode = (SalinasNode) node.jjtGetChild(startIndex);
        if (possibleStepNode.getId() == JJTSTEP) {
            startIndex++;
            SalinasValue newStepValue = SalinasInterpreter.interpret(
                    (SalinasNode) possibleStepNode.jjtGetChild(0), context);
            step.setValue(newStepValue.getValue());
        }

        SalinasValue returnValue = SalinasValue.NULL;

        while (ComparativeOp.NOT_EQUAL_TO.apply(indexValue, stopValue)) {
            // Interpret statements inside the loop
            for (int count = startIndex; count < node.jjtGetNumChildren(); count++) {
                final SalinasNode currentNode = (SalinasNode) node.jjtGetChild(count);
                returnValue = SalinasInterpreter.interpret(currentNode, context);

                final SalinasValue returning = (SalinasValue) context.getAttribute("returning",
                        ScriptContext.ENGINE_SCOPE);
                if (returning != null) {
                    return returning;
                }
            }

            // Update index variable
            indexValue.setValue(((BigDecimal) indexValue.getValue()).add(
                    (BigDecimal) step.getValue()));
        }
        // Interpret statements inside the loop one last time
        for (int count = startIndex; count < node.jjtGetNumChildren(); count++) {
            final SalinasValue returning = (SalinasValue) context.getAttribute("returning",
                    ScriptContext.ENGINE_SCOPE);
            if (returning != null) {
                return returning;
            }

            final SalinasNode currentNode = (SalinasNode) node.jjtGetChild(count);
            returnValue = SalinasInterpreter.interpret(currentNode, context);
        }

        return returnValue;
    }
}
