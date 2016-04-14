/*
 * Copyright 2011-2013 i Data Connect!
 */

package com.idataconnect.salinas;

import java.io.Reader;
import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * JSR-233 ScriptEngine implementation for Salinas.
 */
public class SalinasScriptEngine extends AbstractScriptEngine implements Compilable {
    
    private final SalinasScriptEngineFactory factory;

    /**
     * Creates a new Salinas Script Engine instance using the singleton
     * factory instance.
     */
    public SalinasScriptEngine() {
        this(SalinasScriptEngineFactory.getInstance());
    }

    /**
     * Creates a new Salinas Script Engine instance using the given factory.
     * @param factory the JSR-233 script engine factory.
     */
    public SalinasScriptEngine(SalinasScriptEngineFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object eval(String script, ScriptContext context) throws ScriptException {
        return compile(script).eval(context);
    }

    @Override
    public Object eval(Reader script, ScriptContext context) throws ScriptException {
        return compile(script).eval(context);
    }

    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }

    @Override
    public ScriptEngineFactory getFactory() {
        return factory;
    }

    @Override
    public CompiledScript compile(String script) throws ScriptException {
        return SalinasCompiledScript.compile(script, this);
    }

    @Override
    public CompiledScript compile(Reader script) throws ScriptException {
        return SalinasCompiledScript.compile(script, this);
    }
}
