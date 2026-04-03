/*
 * Copyright 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTDATATYPE;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTIDENTIFIER;


/**
 * Function declaration interpreter delegate implementation.
 */
public class FunctionDeclInterpreter implements InterpreterDelegate {

    private static FunctionDeclInterpreter instance;

    /**
     * Gets a singleton instance of the function declaration interpreter
     * delegate instance.
     * @return a singleton instance
     */
    public static FunctionDeclInterpreter getInstance() {
        if (instance == null) {
            instance = new FunctionDeclInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, SalinasExecutionContext context)
            throws SalinasException {
        final SalinasNode firstChild = node.getChild(0);
        SalinasNode identifierNode;

        final boolean identifierPresent = firstChild.getId() == JJTIDENTIFIER;
        final SalinasValue function = new SalinasValue(node, SalinasType.FUNCTION);

        if (identifierPresent) {
            identifierNode = firstChild;
            context.setGlobalVariable((String) identifierNode.jjtGetValue(), function);
            if (identifierNode.jjtGetNumChildren() > 0) {
                // has strong type
                assert ((SalinasNode) identifierNode.jjtGetChild(0)).getId()
                        == JJTDATATYPE;
                final SalinasType dataType = (SalinasType) ((SalinasNode)
                        identifierNode.jjtGetChild(0)).jjtGetValue();
                function.setStrongType(dataType);
            }
        }

        return function;
    }
}
