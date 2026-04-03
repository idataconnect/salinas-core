/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.data.ConversionException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import java.util.Optional;
import javax.script.ScriptContext;

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
        
        // Functions are stored as global variables in ENGINE_SCOPE
        Object attr = functionContext.getScriptContext().getAttribute(name.toUpperCase(), ScriptContext.ENGINE_SCOPE);
        if (attr == null) {
            attr = functionContext.getScriptContext().getAttribute(name.toUpperCase(), ScriptContext.GLOBAL_SCOPE);
        }
        
        if (attr == null) {
            return Optional.empty();
        }
        
        SalinasValue var = SalinasValue.valueOf(attr);
        
        if (var.getCurrentType() != SalinasType.FUNCTION) {
            return Optional.empty();
        }

        return Optional.of(new UserDefinedFunction(var));
    }
}
