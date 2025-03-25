/*
 * Copyright 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.interpreter;

import com.idataconnect.salinas.SalinasException;
import com.idataconnect.salinas.data.ConversionException;
import com.idataconnect.salinas.data.SalinasType;
import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.parser.SalinasNode;
import com.idataconnect.salinas.parser.SalinasParserTreeConstants;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
                AssignInterpreter.getInstance());
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
        assert delegate != null : "No interpreter delegate for " + SalinasParserTreeConstants.jjtNodeName[node.getId()];

        return delegate.interpret(node, context);
    }

    /**
     * Import all named functions in the current node scope, for forward
     * reference in the script context. This is generally called on the root
     * node of the AST of each script.
     *
     * @param node the node to import functions from
     * @param context the script context
     */
    public static void importFunctions(SalinasNode node, ScriptContext context) {
        if (node.getId() == JJTFUNCTIONDECLARATION) {
            final SalinasNode firstChild = (SalinasNode) node.jjtGetChild(0);
            if (firstChild.getId() == JJTIDENTIFIER) {
                final String name = (String) firstChild.jjtGetValue();
                final SalinasValue functionValue = new SalinasValue(
                        node, SalinasType.FUNCTION);
                context.getBindings(ScriptContext.ENGINE_SCOPE)
                        .put(name.toUpperCase(), functionValue);
            }
        }
    }

    static InterpreterDelegate getDelegate(int nodeId) {
        return delegateMap.get(Integer.valueOf(nodeId));
    }

    /**
     * Sets the variable into the given node. This acts as a general helper function
     * for setting variables in the Salinas AST.
     *
     * @param node the node to set the variable in
     * @param name the name of the variable
     * @param value the value of the variable
     * @param context the script context
     * @return the variable, holding the value that was set
     * @throws ConversionException if the variable exists and is an incompatible strong type
     */
    public static SalinasValue setVariable(SalinasNode node, String name, SalinasValue value, ScriptContext context)
            throws ConversionException {
        SalinasNode identifierNode = node.getChild(0);
        Optional<SalinasValue> variable = node.getVariable(
                (String) identifierNode.jjtGetValue(), context);

        if (variable.isPresent()) {
            variable.get().setValue(value);
            return variable.get();
        } else {
            return ((SalinasNode) node.jjtGetParent()).getFirstVariableHolder()
                    .setVariable((String) identifierNode.jjtGetValue(), value, context);
        }
    }
}
