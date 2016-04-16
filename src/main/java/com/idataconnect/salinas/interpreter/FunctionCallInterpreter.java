/*
 * Copyright 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.function.CallStack;
import com.idataconnect.salinas.function.Function;
import com.idataconnect.salinas.function.FunctionCallException;
import com.idataconnect.salinas.function.FunctionContext;
import com.idataconnect.salinas.function.UserDefinedFunction;
import com.idataconnect.salinas.function.StackFrame;
import com.idataconnect.salinas.parser.SalinasNode;
import javax.script.ScriptContext;

/**
 * Interpreter delegate implementation for function call nodes.
 */
public class FunctionCallInterpreter implements InterpreterDelegate {

    private static FunctionCallInterpreter instance;

    /**
     * Gets a singleton instance of the function call interpreter delegate.
     * @return a singleton instance
     */
    public static FunctionCallInterpreter getInstance() {
        if (instance == null) {
            instance = new FunctionCallInterpreter();
        }

        return instance;
    }

    @Override
    public SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {

        assert node.jjtGetNumChildren() > 0;
        final SalinasNode identifierNode = (SalinasNode) node.jjtGetChild(0);
        final FunctionContext functionContext = (FunctionContext) context
                .getAttribute("salinasFunctionContext", ScriptContext.ENGINE_SCOPE);
        Function function = functionContext.getFunction(
                (String) identifierNode.jjtGetValue(), node);
        
        if (function == null) {
            throw new FunctionCallException("Function "
                    + identifierNode.jjtGetValue() + " not found",
                    node.getFilename(), node.getBeginLine(), node.getBeginColumn());
        }
        
        SalinasValue returnValue;
        int segmentCount = 1;

        do {
            // Fetch the parameters that were passed to the function
            final SalinasNode segmentNode =
                    (SalinasNode) node.jjtGetChild(segmentCount);
            final SalinasValue[] parameters
                    = new SalinasValue[segmentNode.jjtGetNumChildren()];
            for (int count = 0; count < segmentNode.jjtGetNumChildren(); count++) {
                parameters[count] = SalinasInterpreter.interpret(
                        (SalinasNode) segmentNode.jjtGetChild(count), context);
            }

            // Push the calling node onto the call stack
            final CallStack callStack = (CallStack) context.getAttribute(
                    "salinasCallStack", ScriptContext.ENGINE_SCOPE);
            callStack.push(node);
            returnValue = function.call(context, parameters);
            final StackFrame pushedFrame = callStack.pop();
            assert pushedFrame.getNode() == node : "Call stack unbalanced";

            if (node.jjtGetNumChildren() > ++segmentCount) {
                // Chained function call
                if (returnValue.getCurrentType() == SalinasType.FUNCTION) {
                    function = new UserDefinedFunction(returnValue, functionContext);
                } else {
                    throw new FunctionCallException("Attempted chained function "
                            + "call when return type is not a function (Type is " + returnValue.getCurrentType() + ")",
                            node.getFilename(), node.getBeginLine(), node.getBeginColumn());
                }
            } else {
                function = null;
            }
        } while (function != null);
        return returnValue;
    }
}
