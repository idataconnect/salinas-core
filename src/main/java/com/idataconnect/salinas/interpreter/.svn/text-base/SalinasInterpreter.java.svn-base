/*
 * Copyright 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.ConversionException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptContext;

/**
 * Interprets nodes using a script context. This is the main entry point for
 * interpreting Salinas code.
 */
public class SalinasInterpreter {

    /**
     * A map of AST IDs to interpreter delegates assigned to interpret the
     * node. When more node types are added to salinas.jjt, delegates should
     * be added to this map. Some nodes are handled by the delegates directly,
     * and do not need to be added to this map.
     */
    private static final Map<Integer, InterpreterDelegate> delegateMap
            = new HashMap<Integer, InterpreterDelegate>(64, 0.5f);

    static {
        delegateMap.put(JJTSALINASSCRIPT,
                StatementInterpreter.getInstance());
        delegateMap.put(JJTSTATEMENT,
                StatementInterpreter.getInstance());
        delegateMap.put(JJTIDENTIFIER,
                ExpressionInterpreter.getInstance());
        delegateMap.put(JJTMULTIPLICATIVE,
                ExpressionInterpreter.getInstance());
        delegateMap.put(JJTADDITIVE,
                ExpressionInterpreter.getInstance());
        delegateMap.put(JJTASSIGN,
                ExpressionInterpreter.getInstance());
        delegateMap.put(JJTEQUALITY,
                ExpressionInterpreter.getInstance());
        delegateMap.put(JJTCOMPARE,
                ExpressionInterpreter.getInstance());
        delegateMap.put(JJTNUMBER,
                LiteralInterpreter.getInstance());
        delegateMap.put(JJTSTRING,
                LiteralInterpreter.getInstance());
        delegateMap.put(JJTBOOLEAN,
                LiteralInterpreter.getInstance());
        delegateMap.put(JJTDATE,
                LiteralInterpreter.getInstance());
        delegateMap.put(JJTNULL,
                LiteralInterpreter.getInstance());
        delegateMap.put(JJTPRINT,
                PrintInterpreter.getInstance());
        delegateMap.put(JJTPRINTLN,
                PrintInterpreter.getInstance());
        delegateMap.put(JJTPRINTPRELN,
                PrintInterpreter.getInstance());
        delegateMap.put(JJTCONTAINS,
                ContainsInterpreter.getInstance());
        delegateMap.put(JJTAND,
                BooleanInterpreter.getInstance());
        delegateMap.put(JJTOR,
                BooleanInterpreter.getInstance());
        delegateMap.put(JJTBOOLEANNOT,
                BooleanInterpreter.getInstance());
        delegateMap.put(JJTIFBLOCK,
                IfInterpreter.getInstance());
        delegateMap.put(JJTFORLOOP,
                ForInterpreter.getInstance());
        delegateMap.put(JJTCASEBLOCK,
                CaseInterpreter.getInstance());
        delegateMap.put(JJTWHILELOOP,
                WhileInterpreter.getInstance());
        delegateMap.put(JJTFUNCTIONDECLARATION,
                FunctionDeclInterpreter.getInstance());
        delegateMap.put(JJTRETURN,
                ReturnInterpreter.getInstance());
        delegateMap.put(JJTFUNCTIONCALL,
                FunctionCallInterpreter.getInstance());
        delegateMap.put(JJTARRAYACCESS,
                ArrayAccessInterpreter.getInstance());
        delegateMap.put(JJTARRAYLITERAL,
                ArrayLiteralInterpreter.getInstance());
        delegateMap.put(JJTSET,
                SetInterpreter.getInstance());
        delegateMap.put(JJTEXPONENT,
                ExpressionInterpreter.getInstance());
    }

    private SalinasInterpreter() {}

    /**
     * Interprets the given node using state from the given context. Sub-nodes
     * will be interpreted recursively as required, by each interpreter delegate.
     *
     * @param node the node to interpret
     * @param context the JSR-233 script context
     * @return the result from interpreting the node, as a value
     * @throws SalinasException if an error occurs
     */
    public static SalinasValue interpret(SalinasNode node, ScriptContext context)
            throws SalinasException {
        SalinasValue returning = (SalinasValue) context.getAttribute("returning",
                ScriptContext.ENGINE_SCOPE);
        if (returning != null) {
            return returning;
        }
        
        InterpreterDelegate delegate = getDelegate(node.getId());
        assert delegate != null : "No interpreter delegate for node ID " + node.getId();

        return delegate.interpret(node, context);
    }

    /**
     * Recursively import all named functions in the AST.
     *
     * @param node the node to import functions from (generally called with
     * the AST root node)
     * @param context the script context
     */
    public static void importFunctions(SalinasNode node, ScriptContext context) {

        for (int count = 0; count < node.jjtGetNumChildren(); count++) {
            final SalinasNode child = (SalinasNode) node.jjtGetChild(count);
            importFunctions(child, context);
        }
        
        if (node.getId() == JJTFUNCTIONDECLARATION) {
            final SalinasNode firstChild = (SalinasNode) node.jjtGetChild(0);
            if (firstChild.getId() == JJTIDENTIFIER) {
                final String name = (String) firstChild.jjtGetValue();
                final SalinasValue functionValue = new SalinasValue(
                        node, SalinasType.FUNCTION);
                try {
                    setPublicVariable(name, functionValue, context);
                } catch (ConversionException ex) {
                    // Shouldn't happen
                    Logger.getLogger(SalinasInterpreter.class.getName())
                            .log(Level.WARNING, "Conversion error while "
                            + "importing function {0}",
                            name);
                }
            }
        }
    }

    /**
     * Sets a public variable into the given script context.
     *
     * @param name the name of the variable
     * @param value the value of the variable
     * @param context the script context
     */
    public static void setPublicVariable(String name, SalinasValue value,
            ScriptContext context) throws ConversionException {
        final Bindings publicScope = context.getBindings(ScriptContext.ENGINE_SCOPE);
        Object o = publicScope.get(name);
        if (o == null || ! (o instanceof SalinasValue) || ((SalinasValue) value)
                .getStrongType() != SalinasType.UNDEFINED) {
            publicScope.put(name.toUpperCase(), value);
        } else {
            final SalinasValue existing = (SalinasValue) o;
            existing.setValue(value);
        }
    }

    /**
     * Gets a public variable.
     *
     * @param name the name of the variable
     * @param context the script context
     * @return the value of the variable, or <code>null</code> if it did not
     * exist
     */
    public static SalinasValue getPublicVariable(String name, ScriptContext context) {
        final Bindings publicScope = context.getBindings(ScriptContext.ENGINE_SCOPE);
        final Object o = publicScope.get(name.toUpperCase());
        if (o == null || ! (o instanceof SalinasValue)) {
            return null;
        } else {
            return (SalinasValue) o;
        }
    }

    static InterpreterDelegate getDelegate(int nodeId) {
        return delegateMap.get(Integer.valueOf(nodeId));
    }
}
