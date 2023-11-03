/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.data.ConversionException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import java.util.Optional;

/**
 * A function provider implementation which calls user defined functions.
 */
public class UserDefinedFunctionProvider extends FunctionProvider {

    private final FunctionContext functionContext;

    /**
     * Creates a new instance of user defined function provider with the
     * given function context.
     * @param functionContext the function context
     */
    public UserDefinedFunctionProvider(FunctionContext functionContext) {
        this.functionContext = functionContext;
    }

    @Override
    public Optional<Function> getFunction(String name, SalinasNode node)
            throws ConversionException {
        Optional<SalinasValue> var = node.getVariable(name, functionContext.getScriptContext());
        if (!var.isPresent()) {
            return Optional.empty();
        } else if (var.get().getCurrentType() != SalinasType.FUNCTION) {
            throw new ConversionException(name
                    + " is not a function; Its type is " + var.get().getCurrentType(),
                    node.getFilename(), node.getBeginLine(), node.getBeginColumn());
        }

        return Optional.of(new UserDefinedFunction(var.get(), functionContext));
    }
}
