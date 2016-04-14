/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTCASE;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTOTHERWISE;
import javax.script.ScriptContext;

/**
 * Interpreter delegate implementation for DO CASE statement nodes.
 */
public class CaseInterpreter implements InterpreterDelegate {

    private static CaseInterpreter instance;

    /**
     * Gets a singleton instance of the case interpreter.
     * @return a singleton instance
     */
    public static CaseInterpreter getInstance() {
        if (instance == null) {
            instance = new CaseInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        SalinasValue returnValue = SalinasValue.NULL;
        for (int childCount = 0; childCount < node.jjtGetNumChildren(); childCount++) {
            final SalinasNode currentNode = node.getChild(childCount);
            assert currentNode.getId() == JJTCASE
                    || currentNode.getId() == JJTOTHERWISE;
            if (currentNode.getId() == JJTCASE) {
                final SalinasNode expressionNode = currentNode.getChild(0);
                final SalinasValue result = SalinasInterpreter
                        .interpret(expressionNode, context);
                if (result.asType(SalinasType.BOOLEAN).equals(Boolean.TRUE)) {
                    // Case branch found; execute
                    for (int statementCount = 1;
                            statementCount < currentNode.jjtGetNumChildren();
                            statementCount++) {
                        returnValue = SalinasInterpreter.interpret(
                                currentNode.getChild(statementCount), context);

                        final SalinasValue returning = (SalinasValue) context
                                .getAttribute("returning", ScriptContext.ENGINE_SCOPE);
                        if (returning != null) {
                            return returning;
                        }
                    }
                    break;
                }
            } else if (currentNode.getId() == JJTOTHERWISE) {
                // Otherwise branch hit after no case statements hit; execute
                for (int statementCount = 0;
                        statementCount < currentNode.jjtGetNumChildren();
                        statementCount++) {
                    returnValue = SalinasInterpreter.interpret(
                            (SalinasNode) currentNode.jjtGetChild(statementCount),
                            context);

                    final SalinasValue returning = (SalinasValue) context
                            .getAttribute("returning", ScriptContext.ENGINE_SCOPE);
                    if (returning != null) {
                        return returning;
                    }
                }
                break;
            }
        }

        return returnValue;
    }
}
