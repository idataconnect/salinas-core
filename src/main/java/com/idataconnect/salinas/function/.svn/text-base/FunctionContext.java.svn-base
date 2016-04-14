/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.ConversionException;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import java.util.LinkedList;
import java.util.List;
import javax.script.ScriptContext;

/**
 * The function context, which is created once for each <code>ScriptEngine</code>
 * instance, routes function calls to the function providers.
 */
public class FunctionContext {

    private static final InternalFunctionProvider internalProviderInstance
            = new InternalFunctionProvider();

    private UserDefinedFunctionProvider userDefinedProvider
            = new UserDefinedFunctionProvider(this);
    private ScriptContext scriptContext;
    private final List<FunctionProvider> providers
            = new LinkedList<FunctionProvider>();

    /**
     * Creates a new function context.
     *
     * @param scriptContext the salinas context to attach the function context
     * to, for function name binding purposes
     */
    public FunctionContext(ScriptContext scriptContext) {
        this.scriptContext = scriptContext;

        providers.add(internalProviderInstance);
        providers.add(userDefinedProvider);
    }

    /**
     * Gets the script context that this function context is attached to.
     * @return the script context
     */
    public ScriptContext getScriptContext() {
        return scriptContext;
    }

    public Function getFunction(String name, SalinasNode node)
            throws ConversionException{
        Function function = null;
        for (FunctionProvider provider : providers) {
            function = provider.getFunction(name, node);
            if (function != null) {
                break;
            }
        }

        return function;
    }

    /**
     * Attempts to call the function with the given name, using the given
     * parameters.
     * <p>
     * This will iterate through all function providers until a match is found,
     * starting with the internal function provider.
     *
     * @param name the name of the function
     * @param node the node where the function is being called from
     * @param parameters the parameters to pass to the function
     * @return the result returned by the function
     * @throws FunctionCallException if the function does not exist, is called
     * with an incorrect number of parameters, or another form of invocation
     * error occurs
     * @throws SalinasException if an error occurs during execution of the
     * function
     */
    public SalinasValue call(String name, SalinasNode node, SalinasValue... parameters)
            throws FunctionCallException, SalinasException {

        Function function = null;
        for (FunctionProvider provider : providers) {
            function = provider.getFunction(name, node);
            if (function != null) {
                break;
            }
        }

        if (function == null) {
            throw new FunctionCallException("Unknown function: " + name,
                    node.getFilename(), node.getBeginLine(), node.getBeginColumn());
        }

        return function.call(getScriptContext(), parameters);
    }
}
