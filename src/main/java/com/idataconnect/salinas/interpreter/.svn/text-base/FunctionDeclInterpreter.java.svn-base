/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTDATATYPE;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTIDENTIFIER;
import javax.script.ScriptContext;

/**
 * Function declaration interpreter delegate implementation.
 */
public class FunctionDeclInterpreter implements InterpreterDelegate {

    private static FunctionDeclInterpreter instance;

    /**
     * Gets a singleton instance of the functinon declaration intrepreter
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
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        final SalinasNode firstChild = (SalinasNode) node.jjtGetChild(0);
        SalinasNode identifierNode;

        final boolean identifierPresent = firstChild.getId() == JJTIDENTIFIER;
        final SalinasValue function = new SalinasValue(node, SalinasType.FUNCTION);

        if (identifierPresent) {
            // TODO consolidate variable setting routines
            identifierNode = (SalinasNode) node
                    .jjtGetChild(0);
            SalinasValue value = node.getVariable(
                    (String) identifierNode.jjtGetValue(), context);

            if (value != null) {
                value.setValue(function);
            } else {
                ((SalinasNode) node.jjtGetParent()).getFirstVariableHolder()
                        .setVariable((String) identifierNode.jjtGetValue(),
                        function);
            }
            if (identifierNode.jjtGetNumChildren() > 0) {
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
