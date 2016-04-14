/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.ConversionException;
import com.idataconnect.salinas.data.SalinasArrayMap;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import javax.script.ScriptContext;

/**
 * Interpreter delegate implementation for array access statement nodes.
 */
public class ArrayAccessInterpreter implements InterpreterDelegate {
    
    private static ArrayAccessInterpreter instance;

    /**
     * Gets a singleton instance of the array access interpreter delegate.
     *
     * @return a singleton instance
     */
    public static ArrayAccessInterpreter getInstance() {
        if (instance == null) {
            instance = new ArrayAccessInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        // ArrayAccess
        // L Identifier
        // L ArrayAccessSegment
        //   L Expression
        // L ArrayAccessSegment
        //   L Expression
        // L ...
        final SalinasNode identifierNode = (SalinasNode) node.jjtGetChild(0);
        assert identifierNode.getId() == JJTIDENTIFIER;
        // Try to find an existing variable
        final SalinasValue existingArray = node.getVariable(
                (String) identifierNode.jjtGetValue(), context);
        if (existingArray != null) {
            if (existingArray.getCurrentType() != SalinasType.ARRAY) {
                throw new ConversionException(identifierNode.jjtGetValue()
                        + " is not an array", node.getFilename(),
                        node.getBeginLine(), node.getBeginColumn());
            }

        } else {
            return SalinasValue.UNDEFINED;
        }

        // We now have the array variable; walk the indices
        SalinasValue currentValue = existingArray;
        int index = 1;
        SalinasNode segmentNode;
        SalinasValue expressionValue;
        while (currentValue != null && currentValue.getCurrentType() == SalinasType.ARRAY) {
            segmentNode = node.getChild(index++);
            expressionValue = SalinasInterpreter.interpret(segmentNode.getChild(0), context);
            currentValue = (SalinasValue) ((SalinasArrayMap) currentValue.getValue())
                    .get(expressionValue.getValue());
        }

        if (currentValue == null) {
            currentValue = SalinasValue.UNDEFINED;
        }
        return currentValue;
    }
}
