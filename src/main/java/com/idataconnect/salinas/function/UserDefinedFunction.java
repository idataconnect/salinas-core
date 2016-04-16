/*
 * Copyright 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.ConversionException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.interpreter.SalinasInterpreter;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import javax.script.ScriptContext;

/**
 * A function which is implemented natively in pure Salinas script.
 */
public class UserDefinedFunction extends Function {

    private final SalinasValue functionValue;
    private final FunctionContext functionContext;

    /**
     * Creates a new Salinas function, given the value which contains the
     * parsed function.
     * @param functionValue the salinas value whose <code>getValue()</code>
     * method will return the function node
     * @param functionContext the function context associated with the script
     * context
     */
    public UserDefinedFunction(SalinasValue functionValue, FunctionContext functionContext) {
        this.functionValue = functionValue;
        this.functionContext = functionContext;
    }

    @Override
    public SalinasValue call(ScriptContext context, SalinasValue... parameters) throws SalinasException {
        assert functionValue.getValue() instanceof SalinasNode
                : "Raw function value should be of type SalinasNode but it is of type "
                + functionValue.getValue().getClass().getName();
        final SalinasNode node = (SalinasNode) functionValue.getValue();
        assert node.getId() == JJTFUNCTIONDECLARATION
                : "Function node is not a function declaration";

        // Apply all of the parameters to the function
        int parameterCount = 0;
        for (int count = 0; count < node.jjtGetNumChildren(); count++) {
            final SalinasNode childNode = (SalinasNode) node.jjtGetChild(count);
            switch (childNode.getId()) {
                case JJTIDENTIFIER:
                    break;
                case JJTFUNCTIONPARAMETER:
                    // Check if a value was passed in for this parameter
                    if (parameters.length > parameterCount) {
                        // Assign the passed value to the parameter variable
                        parameterAssign((SalinasNode) childNode.jjtGetChild(0),
                                parameters[parameterCount++], node, context);
                    } else {
                        // Check whether this parameter has a default value
                        if (childNode.jjtGetNumChildren() > 1) {
                            final SalinasValue defaultValue
                                    = SalinasInterpreter.interpret(
                                            (SalinasNode) childNode.jjtGetChild(1),
                                            functionContext.getScriptContext());
                            // Assign the default value to the parameter variable
                            parameterAssign((SalinasNode) childNode.jjtGetChild(0),
                                    defaultValue, node, context);
                        } else {
                            throw new FunctionCallException("Not enough parameters passed to function");
                        }
                    }   break;
                default:
                    // Statements inside the function
                    SalinasInterpreter.interpret(childNode, functionContext.getScriptContext());
                    SalinasValue returning = (SalinasValue) functionContext
                            .getScriptContext().getAttribute("returning",
                                    ScriptContext.ENGINE_SCOPE);
                    if (returning != null) {
                        functionContext.getScriptContext().removeAttribute("returning",
                                ScriptContext.ENGINE_SCOPE);
                        return returning;
                    }   break;
            }
        }

        return new SalinasValue("Function at " + node.getBeginLine() + "," + node.getBeginColumn());
    }

    /**
     * Assigns the parameter defined in the identifier node to the given value,
     * and stores that value in the given function node.
     * @param identifierNode the identifier node which defined the function
     * parameter
     * @param value the value to assign
     * @param functionNode the function node which should hold the value
     * @throws ConversionException if an error occurs during conversion of values
     */
    void parameterAssign(
            final SalinasNode identifierNode,
            final SalinasValue value,
            final SalinasNode functionNode,
            final ScriptContext context
            ) throws ConversionException {
        SalinasValue existingVar = functionNode.getVariable(
                (String) identifierNode.jjtGetValue(), false, context);
        if (existingVar != null) {
            existingVar.setValue(value);
        } else {
            // TODO consolidate variable setting routines
            final SalinasValue val = value;
            functionNode.setVariable((String) identifierNode.jjtGetValue(),
                    val, context);

            if (identifierNode.jjtGetNumChildren() > 0) {
                // parameter has strong type
                assert ((SalinasNode) identifierNode.jjtGetChild(0)).getId()
                        == JJTDATATYPE;
                final SalinasType dataType
                        = (SalinasType) ((SalinasNode) identifierNode.jjtGetChild(0))
                        .jjtGetValue();
                val.setStrongType(dataType);
            }
        }
    }
}
