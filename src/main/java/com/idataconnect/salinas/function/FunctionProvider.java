/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.data.ConversionException;
import com.idataconnect.salinas.parser.SalinasNode;
import java.util.Optional;

/**
 * Base class for function providers.
 * @see InternalFunctionProvider
 * @see UserDefinedFunctionProvider
 */
public abstract class FunctionProvider {

    /**
     * Gets the function with the given name from the provider. If the provider
     * does not provide the requested function, this method will return
     * an empty optional.
     *
     * @param name the name of the function
     * @param node the Salinas node where the function is being called
     * @return the requested function, or empty if this provider
     * does not provide the requested function
     * @throws ConversionException if the requested function name is defined,
     * but it is not a function
     */
    public abstract Optional<Function> getFunction(String name, SalinasNode node)
            throws ConversionException;
}
