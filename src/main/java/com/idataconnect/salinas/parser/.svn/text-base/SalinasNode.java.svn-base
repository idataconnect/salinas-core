/*
 * Copyright (c) 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.parser;

import com.idataconnect.salinas.data.SalinasValue;
import com.idataconnect.salinas.interpreter.SalinasInterpreter;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptContext;

/**
 * Base class of all Salinas nodes.
 */
public class SalinasNode extends SimpleNode {

    /**
     * The types of nodes which may hold variables. {@link #setVariable} should
     * only be called on nodes of these types. To discover the closest parent in
     * the tree which should hold variables, use
     * {@link #getFirstVariableHolder()}.
     */
    static final int[] VARIABLE_HOLDER_TYPES = {
        JJTSALINASSCRIPT,
        JJTFORLOOP,
        JJTIFBLOCK,
        JJTWHILELOOP,
        JJTCASEBLOCK,
        JJTFUNCTIONDECLARATION,
    };

    private String filename;
    private int beginLine = -1;
    private int beginColumn = -1;
    private Map<String, SalinasValue> variables;

    /**
     * Creates a new Salinas node.
     * @param i the AST ID of the node
     */
    public SalinasNode(int i) {
        super(i);
    }

    /**
     * Creates a new Salinas node, and specifies the parser. This constructor
     * is provided for the benefit of the JSR-233 APIs.
     *
     * @param p the SaliansParser instance that is parsing a script
     * @param i the AST ID of the node
     */
    public SalinasNode(SalinasParser p, int i) {
        super(p, i);
    }

    /**
     * Gets a variable visible to this node. This is equivalent to calling
     * {@link #getVariable(java.lang.String, boolean) getVariable(name, true)}.
     *
     * @param name the name of the variable
     * @param context the script context
     * @return the variable, or <code>null</code> if the variable was not found
     */
    public SalinasValue getVariable(String name, ScriptContext context) {
        return getVariable(name, true, context);
    }

    /**
     * Gets a variable attached to this node. If <code>fullScope</code> is
     * <code>true</code>, parent nodes will be recursively called to fetch
     * the variable until it is found.
     *
     * @param name the name of the variable
     * @param fullScope whether or not to search parent nodes
     * @param context the script context
     * @return the variable, or <code>null</code> if it was not found
     */
    public SalinasValue getVariable(String name, boolean fullScope,
            ScriptContext context) {
        SalinasValue fetchedValue = null;

        if (fullScope) {
            SalinasNode currentNode = this;

            do {
                if (currentNode.variables != null) {
                    fetchedValue = currentNode.variables.get(name.toUpperCase());
                }
                currentNode = (SalinasNode) currentNode.jjtGetParent();
            } while (fetchedValue == null && currentNode != null);
        } else if (variables != null) {
            fetchedValue = variables.get(name.toUpperCase());
        }

        if (fullScope && fetchedValue == null && context != null) {
            // Try to find the variable as a public variable
            fetchedValue = SalinasInterpreter.getPublicVariable(name, context);
        }

        return fetchedValue;
    }

    /**
     * Sets a variable in the current node, so that all children of this
     * node will have the variable in their scope.
     *
     * @param name the name of the variable
     * @param value the value of the variable
     * @return the value that was set
     */
    public SalinasValue setVariable(String name, SalinasValue value) {
        assert getFirstVariableHolder() == this : "Attempted to assign a variable to a node which is not a variable holder. Node ID=" + this.getId();
        if (variables == null) {
            variables = new HashMap<String, SalinasValue>(32);
        }
        return variables.put(name.toUpperCase(), value);
    }

    /**
     * Unsets a variable visible to this node. This is equivalent to calling
     * {@link #unsetVariable(java.lang.String, boolean) unsetVariable(name, true)}.
     *
     * @param name the name of the variable
     * @return the variable, or <code>null</code> if the variable was not found
     */
    public SalinasValue unsetVariable(String name) {
        return unsetVariable(name, true);
    }

    /**
     * Unsets the variable attached to this node.  If <code>fullScope</code> is
     * <code>true</code>, parent nodes will be recursively called to unset
     * the variable until it is found.
     *
     * @param name the name of the variable
     * @param fullScope whether to search parent nodes for the variable to unset
     * @return the variable that was unset, or <code>null</code> if it was not
     * found
     */
    public SalinasValue unsetVariable(String name, boolean fullScope) {
        if (fullScope) {
            SalinasValue returnValue;
            if (variables != null && (returnValue = variables.remove(name.toUpperCase())) != null) {
                return returnValue;
            } else if (jjtGetParent() != null) {
                return ((SalinasNode) jjtGetParent()).unsetVariable(name, true);
            } else {
                return null;
            }
        } else {
            return variables == null ? null : variables.remove(name.toUpperCase());
        }
    }

    /**
     * Gets the abstract syntax tree identifier of the current node. This
     * enables the caller to discover the node type without using reflection.
     *
     * @return the AST ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the source filename that this node was parsed from.
     *
     * @return the filename
     */
    public String getFilename() {
        if (filename == null) {
            // Search parent for the filename
            if (parent != null) {
                setFilename(((SalinasNode) parent).getFilename());
            }
        }
        return filename;
    }

    /**
     * Sets the source filename that this node was parsed from.
     *
     * @param filename the filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Gets the begin column of the first token in this node.
     *
     * @return the start column
     */
    public int getBeginColumn() {
        if (beginColumn == -1) {
            // Search children for the begin column
            if (jjtGetNumChildren() > 0) {
                setBeginColumn(((SalinasNode) jjtGetChild(0)).getBeginColumn());
            }
        }
        return beginColumn;
    }

    /**
     * Sets the begin column of the first token in this node.
     *
     * @param startColumn the start column
     */
    public void setBeginColumn(int startColumn) {
        this.beginColumn = startColumn;
    }

    /**
     * Gets the begin line of the first token in this node.
     *
     * @return the start line
     */
    public int getBeginLine() {
        if (beginLine == -1) {
            // Search children for the begin line
            if (jjtGetNumChildren() > 0) {
                beginLine = ((SalinasNode) jjtGetChild(0)).getBeginLine();
            }
        }
        return beginLine;
    }

    /**
     * Sets the begin line of the first token in this node.
     *
     * @param startLine the start line
     */
    public void setBeginLine(int startLine) {
        this.beginLine = startLine;
    }

    /**
     * Gets the first node in the parent chain that may hold variables.
     *
     * @return a parent node which may hold variables
     */
    public SalinasNode getFirstVariableHolder() {
        SalinasNode currentNode = this;
        do {
            boolean canHold = false;
            for (int type : VARIABLE_HOLDER_TYPES) {
                if (currentNode.getId() == type) {
                    canHold = true;
                    break;
                }
            }

            if (canHold) {
                return currentNode;
            }

            currentNode = (SalinasNode) currentNode.jjtGetParent();
        } while (currentNode != null);

        assert false : "There must be a node in the chain that may hold variables";
        return null;
    }

    /**
     * Wrapper for {@link #jjtGetChild(int)} which returns a
     * <code>SalinasNode</code>, to avoid a cast.
     *
     * @param index the child index
     * @return a Salinas node
     */
    public SalinasNode getChild(int index) {
        return (SalinasNode) jjtGetChild(index);
    }
}
