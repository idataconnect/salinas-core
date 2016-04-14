/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.SalinasValue;
import javax.script.ScriptContext;

/**
 * Base class for callable functions.
 * @see SalinasFunction
 */
public abstract class Function {

    /**
     * Calls the function with the given parameters.
     *
     * @param context the JSR-233 script context instance of the caller
     * @param parameters the parameters to call the function with
     * @return the value that the function returned
     * @throws SalinasException if an error occurred while calling the function
     */
    public abstract SalinasValue call(ScriptContext context, SalinasValue... parameters)
            throws SalinasException;
}
