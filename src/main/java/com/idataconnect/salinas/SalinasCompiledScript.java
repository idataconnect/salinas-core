/*
 * Copyright (c) 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas;

import com.idataconnect.salinas.function.CallStack;
import com.idataconnect.salinas.function.FunctionContext;
import com.idataconnect.salinas.interpreter.SalinasExecutionContext;
import com.idataconnect.salinas.interpreter.SalinasInterpreter;
import com.idataconnect.salinas.parser.ParseException;
import com.idataconnect.salinas.parser.SalinasNode;
import com.idataconnect.salinas.parser.SalinasParser;
import com.idataconnect.salinas.data.SalinasValue;
import java.io.Reader;
import java.io.StringReader;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * Compiled Salinas script implementation.
 */
public class SalinasCompiledScript extends CompiledScript {

    private final SalinasNode ast;
    private final SalinasScriptEngine engine;

    private SalinasCompiledScript(SalinasNode ast, SalinasScriptEngine engine) {
        this.ast = ast;
        this.engine = engine;
    }

    /**
     * Compiles the script contained in the given reader.
     *
     * @param script the reader containing the script
     * @param engine the engine that will run the script
     * @return a compiled script instance
     * @throws ScriptException if a parsing error occurs
     */
    public static SalinasCompiledScript compile(Reader script,
            SalinasScriptEngine engine) throws ScriptException {
        try {
            final SalinasParser parser = new SalinasParser(script);
            final SalinasNode ast = parser.buildAst();
            return new SalinasCompiledScript(ast, engine);
        } catch (ParseException | com.idataconnect.salinas.parser.TokenMgrError ex) {
            final ScriptException se = new ScriptException(ex.getMessage());
            se.initCause(ex);
            throw se;
        }
    }

    /**
     * Compiles the script contained in the given string.
     *
     * @param script the string containing the script
     * @param engine the engine that will run the script
     * @return a compiled script instance
     * @throws ScriptException if a parsing error occurs
     */
    public static SalinasCompiledScript compile(String script,
            SalinasScriptEngine engine) throws ScriptException {
        try {
            final SalinasParser parser = new SalinasParser(new StringReader(script));
            final SalinasNode ast = parser.buildAst();
            return new SalinasCompiledScript(ast, engine);
        } catch (ParseException | com.idataconnect.salinas.parser.TokenMgrError ex) {
            final ScriptException se = new ScriptException(ex.getMessage());
            se.initCause(ex);
            throw se;
        }
    }

    @Override
    public Object eval(ScriptContext context) throws ScriptException {
        if (System.getProperty("salinas.debug.ast") != null) {
            ast.dump("");
        }
        return run(ast, context);
    }

    /**
     * Imports all functions defined in this script into the given context
     * without executing the top-level script code.
     *
     * @param context the script context to import into
     */
    public void importOnly(ScriptContext context) {
        SalinasExecutionContext execContext = new SalinasExecutionContext(context);
        importFunctions(ast, execContext);
    }

    private Object run(SalinasNode node, ScriptContext context) throws ScriptException {
        if (context.getAttribute("salinasConfig") == null) {
            context.setAttribute("salinasConfig", new SalinasConfig(),
                    ScriptContext.ENGINE_SCOPE);
        }
        if (context.getAttribute("salinasCallStack") == null) {
            context.setAttribute("salinasCallStack", new CallStack(),
                    ScriptContext.ENGINE_SCOPE);
        }
        if (context.getAttribute("salinasFunctionContext") == null) {
            context.setAttribute("salinasFunctionContext",
                    new FunctionContext(context),
                    ScriptContext.ENGINE_SCOPE);
        }
        if (context.getAttribute("salinasWorkAreaManager") == null) {
            context.setAttribute("salinasWorkAreaManager",
                    new com.idataconnect.salinas.data.WorkAreaManager(),
                    ScriptContext.ENGINE_SCOPE);
        }

        SalinasExecutionContext execContext = new SalinasExecutionContext(context);
        importFunctions(node, execContext);

        try {
            SalinasValue val = (SalinasValue) SalinasInterpreter.interpret(node, execContext);
            return val == null ? null : val.getValue();
        } catch (SalinasException ex) {
            ScriptException se = new ScriptException(ex.getMessage(), ex.getFilename(),
                    ex.getBeginLine(), ex.getBeginColumn());
            se.addSuppressed(ex);
            throw se;
        }
    }

    private void importFunctions(SalinasNode node, SalinasExecutionContext context) {
        if (node.getId() == com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTFUNCTIONDECLARATION) {
            final SalinasNode firstChild = (SalinasNode) node.jjtGetChild(0);
            if (firstChild.getId() == com.idataconnect.salinas.parser.SalinasParserTreeConstants.JJTIDENTIFIER) {
                final String name = (String) firstChild.jjtGetValue();
                final SalinasValue functionValue = new SalinasValue(
                        node, com.idataconnect.salinas.data.SalinasType.FUNCTION);
                context.setGlobalVariable(name, functionValue);
            }
        }
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            importFunctions((SalinasNode) node.jjtGetChild(i), context);
        }
    }

    @Override
    public ScriptEngine getEngine() {
        return engine;
    }
}
