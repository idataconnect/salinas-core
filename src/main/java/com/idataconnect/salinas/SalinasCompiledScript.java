/*
 * Copyright 2011-2016 i Data Connect!
 */

package com.idataconnect.salinas;

import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.function.CallStack;
import com.idataconnect.salinas.function.FunctionContext;
import com.idataconnect.salinas.interpreter.SalinasInterpreter;
import com.idataconnect.salinas.io.TerminatingReader;
import com.idataconnect.salinas.parser.ParseException;
import com.idataconnect.salinas.parser.SalinasNode;
import com.idataconnect.salinas.parser.SalinasParser;
import com.idataconnect.salinas.parser.TokenMgrError;
import java.io.*;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

/**
 * JSR-233 CompiledScript implementation for Salinas. Salinas is tightly
 * coupled with the JSR-233 APIs and this class kickstarts the interpreter.
 */
public class SalinasCompiledScript extends CompiledScript {
    
    private final SalinasNode ast;
    private final ScriptEngine engine;
    private final String filename;

    /**
     * Creates a compiled script instance using the root of the abstract
     * syntax tree and the script engine.
     * @param ast the root of the abstract syntax tree
     * @param engine the script engine used to parse the script
     */
    public SalinasCompiledScript(SalinasNode ast, ScriptEngine engine) {
        this.ast = ast;
        this.engine = engine;
        this.filename = "<unknown>";
    }

    /**
     * Creates a compiled script instance using the root of the abstract
     * syntax tree, the script engine, and the filename of the script being
     * parsed.
     * @param ast the root of the abstract syntax tree
     * @param engine the script engine used to parse the script
     * @param filename the filename of the script being parsed
     */
    public SalinasCompiledScript(SalinasNode ast, ScriptEngine engine, String filename) {
        this.ast = ast;
        this.engine = engine;
        this.filename = filename;
    }

    /**
     * Compiles a script in string form.
     * @param script the script to compile
     * @param engine the script engine used to parse the script
     * @return a compiled script instance
     * @throws ScriptException if an error occurs during compilation
     */
    public static SalinasCompiledScript compile(String script, ScriptEngine engine)
            throws ScriptException {
        return compile(new StringReader(script), engine);
    }

    /**
     * Compiles a script from the given reader.
     * @param reader the reader to read the script from
     * @param engine the script engine used to parse the script
     * @return a compiled script instance
     * @throws ScriptException if an error occurs during compilation
     */
    public static SalinasCompiledScript compile(final Reader reader,
            final ScriptEngine engine) throws ScriptException {
        return compile(reader, engine, "<unknown>");
    }

    /**
     * Compiles a script using the given file.
     * @param file the file to read the script from
     * @param engine the engine used to parse the script
     * @return a compiled script instance
     * @throws ScriptException if an error occurs during compilation
     */
    public static SalinasCompiledScript compile(final File file,
            final ScriptEngine engine) throws ScriptException {
        try {
            return compile(new FileReader(file), engine, file.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            throw new ScriptException("File not found: " + file.getAbsolutePath());
        }
    }

    /**
     * Compiles a script using the given reader, the given script engine,
     * and the given filename that the reader is reading the script from.
     * @param reader the reader to read the script from
     * @param engine the engine used to parse the script
     * @param filename the filename that the reader is reading the script from
     * @return a compiled script instance
     * @throws ScriptException if an error occurs during compilation
     */
    public static SalinasCompiledScript compile(final Reader reader,
            final ScriptEngine engine,
            final String filename)
            throws ScriptException {
        try {
            SalinasParser parser = new SalinasParser(new TerminatingReader(reader));
            SalinasNode root = parser.buildAst();
            SalinasInterpreter.importFunctions(root, engine.getContext());
            root.setFilename(filename);
            return new SalinasCompiledScript(root, engine, filename);
        } catch (ParseException ex) {
            throw new ScriptException("Parse error: " + ex.getMessage(),
                    filename, ex.currentToken.next.beginLine,
                    ex.currentToken.next.beginColumn);
        } catch (TokenMgrError ex) {
            final ScriptException se = new ScriptException(ex.getMessage());
            se.initCause(ex);
            throw se;
        }
    }

    @Override
    public Object eval(ScriptContext context) throws ScriptException {
        ast.dump("");
        return run(ast, context);
    }

    private Object run(SalinasNode node, ScriptContext context) throws ScriptException {
        SalinasConfig config = (SalinasConfig) context.getAttribute("salinasConfig");
        if (config == null) {
            config = new SalinasConfig();
            context.setAttribute("salinasConfig", config,
                    ScriptContext.ENGINE_SCOPE);
            context.setAttribute("salinasCallStack", new CallStack(),
                    ScriptContext.ENGINE_SCOPE);
            context.setAttribute("salinasFunctionContext",
                    new FunctionContext(context),
                    ScriptContext.ENGINE_SCOPE);
        }

        try {
            SalinasValue val = (SalinasValue) SalinasInterpreter.interpret(node, context);
            return val == null ? null : val.getValue();
        } catch (SalinasException ex) {
            throw new ScriptException(ex.getMessage(), ex.getFilename(),
                    ex.getBeginLine(), ex.getBeginColumn());
        }
    }

    @Override
    public ScriptEngine getEngine() {
        return engine;
    }
}
