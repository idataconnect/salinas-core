/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTSTATEMENT;
import javax.script.ScriptContext;

/**
 * Interpreter delegate implementation for IF statement nodes.
 */
public class IfInterpreter implements InterpreterDelegate {

    private static IfInterpreter instance;

    /**
     * Gets a singleton instance of the IF statement interpreter delegate.
     * @return a singleton instance
     */
    public static IfInterpreter getInstance() {
        if (instance == null) {
            instance = new IfInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        // IfBlock
        // L IfBranch
        //   L Expression (if or elseif)
        // ...
        // L IfBranch
        //   L Statement (else)
        assert node.jjtGetNumChildren() >= 1;
        SalinasValue value = SalinasValue.NULL;
        
        for (int branchCount = 0; branchCount < node.jjtGetNumChildren(); branchCount++) {
            final SalinasNode branchNode = (SalinasNode) node.jjtGetChild(branchCount);
            if (branchNode.getId() == JJTSTATEMENT) {
                // else
                for (int statementCount = 0; statementCount < branchNode.jjtGetNumChildren(); statementCount++) {
                    value = SalinasInterpreter.interpret(
                            (SalinasNode) branchNode.jjtGetChild(statementCount), context);
                }
                break;
            } else {
                // if or elseif
                final SalinasNode expressionNode
                        = (SalinasNode) branchNode.jjtGetChild(0);
                if (SalinasInterpreter.interpret(expressionNode, context)
                        .asType(SalinasType.BOOLEAN).equals(Boolean.TRUE)) {
                    for (int statementCount = 1; statementCount < branchNode.jjtGetNumChildren(); statementCount++) {
                        value = SalinasInterpreter.interpret(
                                (SalinasNode) branchNode.jjtGetChild(statementCount), context);
                    }
                    break;
                }
            }
        }

        return value;
    }
}
