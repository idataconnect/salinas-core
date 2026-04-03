/*
 * Copyright 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.interpreter.SalinasExecutionContext;
import com.idataconnect.salinas.interpreter.SalinasInterpreter;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import javax.script.ScriptContext;


/**
 * A function which is implemented natively in pure Salinas script.
 */
public class UserDefinedFunction extends Function {

    private final SalinasValue functionValue;

    /**
     * Creates a new Salinas function, given the value which contains the
     * parsed function.
     * @param functionValue the salinas value whose <code>getValue()</code>
     * method will return the function node
     */
    public UserDefinedFunction(SalinasValue functionValue) {
        this.functionValue = functionValue;
    }

    /**
     * Old constructor for backward compatibility if needed, but the second arg is unused now.
     */
    public UserDefinedFunction(SalinasValue functionValue, FunctionContext functionContext) {
        this(functionValue);
    }

    @Override
    public SalinasValue call(SalinasExecutionContext context, SalinasValue... parameters) throws SalinasException {
        assert functionValue.getValue() instanceof SalinasNode;
        final SalinasNode node = (SalinasNode) functionValue.getValue();
        
        context.pushScope();
        try {
            int parameterCount = 0;
            for (int count = 0; count < node.jjtGetNumChildren(); count++) {
                final SalinasNode childNode = (SalinasNode) node.jjtGetChild(count);
                switch (childNode.getId()) {
                    case JJTIDENTIFIER:
                        // Function name, skip
                        break;
                    case JJTFUNCTIONPARAMETER:
                        final SalinasNode identifierNode = (SalinasNode) childNode.jjtGetChild(0);
                        final String paramName = (String) identifierNode.jjtGetValue();
                        SalinasValue paramValue;

                        if (parameters.length > parameterCount) {
                            paramValue = parameters[parameterCount++];
                        } else if (childNode.jjtGetNumChildren() > 1) {
                            paramValue = SalinasInterpreter.interpret((SalinasNode) childNode.jjtGetChild(1), context);
                        } else {
                            throw new FunctionCallException("Not enough parameters passed to function " + paramName);
                        }

                        // Apply strong type if present
                        if (identifierNode.jjtGetNumChildren() > 0) {
                            final SalinasType dataType = (SalinasType) ((SalinasNode) identifierNode.jjtGetChild(0)).jjtGetValue();
                            paramValue.setStrongType(dataType);
                        }
                        
                        context.setVariable(paramName, paramValue);
                        break;
                    default:
                        // Execute statement
                        SalinasInterpreter.interpret(childNode, context);
                        
                        // Check for return
                        SalinasValue returning = (SalinasValue) context.getScriptContext().getAttribute("returning", ScriptContext.ENGINE_SCOPE);
                        if (returning != null) {
                            context.getScriptContext().removeAttribute("returning", ScriptContext.ENGINE_SCOPE);
                            return returning;
                        }
                        break;
                }
            }
            return SalinasValue.NULL;
        } finally {
            context.popScope();
        }
    }
}
