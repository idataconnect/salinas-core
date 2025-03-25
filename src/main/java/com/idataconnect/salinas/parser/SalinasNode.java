/*
 * Copyright (c) 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.parser;

import com.idataconnect.salinas.data.SalinasValue;
import static com.idataconnect.salinas.parser.SalinasParserTreeConstants.*;
import java.util.Locale;
import java.util.Optional;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;

/**
 * Base class of all Salinas nodes.
 */
public class SalinasNode extends SimpleNode {

    /**
     * The types of nodes that may hold variables. {@link #setVariable} should
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
     * {@link #getVariable(String, boolean) getVariable(name, true)}.
     *
     * @param name the name of the variable
     * @param context the script context
     * @return the optional variable value, present if the variable was found
     */
    public Optional<SalinasValue> getVariable(String name, ScriptContext context) {
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
     * @return the optional variable value, present if the variable was found
     */
    public Optional<SalinasValue> getVariable(String name, boolean fullScope,
            ScriptContext context) {
        Object fetchedValue = null;

        Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);

        SalinasNode currentNode = this;

        final String nameKey = nameKey(name);

        do {
            fetchedValue = bindings.get(String.valueOf(currentNode.hashCode()));
            if (fetchedValue instanceof Bindings) {
                fetchedValue = ((Bindings) fetchedValue).get(nameKey);
            } else if (fetchedValue != null) {
                return Optional.of(SalinasValue.valueOf(fetchedValue));
            }
            currentNode = fullScope ? (SalinasNode) currentNode.jjtGetParent()
                                    : null;
        } while (fetchedValue == null && currentNode != null);

        if (fetchedValue == null) {
            if ((fetchedValue = bindings.get(nameKey(name))) == null) {
                return Optional.empty();
            }
        }

        return Optional.of(SalinasValue.valueOf(fetchedValue));
    }

    /**
     * Gets the key that the name will be stored under, in the bindings.
     * @param name the name that will be converted to a bindings key
     * @return the key that the name will be stored under, in the bindings
     */
    static String nameKey(String name) {
        return name.toUpperCase(Locale.ROOT);
    }

    /**
     * Sets a variable in the current node, so that all children of this
     * node will have the variable in their scope.
     *
     * @param name the name of the variable
     * @param value the value of the variable
     * @param context the script context
     * @return the value that was set
     */
    public SalinasValue setVariable(String name, SalinasValue value,
            ScriptContext context) {
        assert getFirstVariableHolder() == this : "Attempted to assign a variable to a node that is not a variable holder. Node ID=" + this.getId();
        final Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
        if (jjtGetParent() == null) {
            // Global
            return (SalinasValue) bindings.put(nameKey(name), value);
        } else {
            Bindings nodeBindings = (Bindings) bindings.get(String.valueOf(hashCode()));
            if (nodeBindings == null) {
                nodeBindings = new SimpleBindings();
                bindings.put(String.valueOf(hashCode()), nodeBindings);
            }
            return (SalinasValue) nodeBindings.put(nameKey(name), value);
        }
    }

    /**
     * Unsets a variable visible to this node. This is equivalent to calling
     * {@link #unsetVariable(java.lang.String, boolean) unsetVariable(name, true)}.
     *
     * @param name the name of the variable
     * @param context the script context
     * @return the variable, or <code>null</code> if the variable was not found
     */
    public SalinasValue unsetVariable(String name, ScriptContext context) {
        return unsetVariable(name, true, context);
    }

    /**
     * Unsets the variable attached to this node.  If <code>fullScope</code> is
     * <code>true</code>, parent nodes will be recursively called to unset
     * the variable until it is found.
     *
     * @param name the name of the variable
     * @param fullScope whether to search parent nodes for the variable to unset
     * @param context the script context
     * @return the variable that was unset, or <code>null</code> if it was not
     * found
     */
    public SalinasValue unsetVariable(String name, boolean fullScope,
        ScriptContext context) {
        final Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
        if (fullScope) {
            SalinasNode currentNode = this;
            while (currentNode != null) {
                final Bindings currentBindings = (Bindings) bindings.get(String.valueOf(hashCode()));
                if (currentBindings != null) {
                    SalinasValue returnValue = (SalinasValue) currentBindings.remove(String.valueOf(currentNode.hashCode()));
                    if (returnValue != null) {
                        return returnValue;
                    }
                }
                currentNode = (SalinasNode) currentNode.jjtGetParent();
            }

            return null; // Not found
        } else {
            return (SalinasValue) bindings.remove(name);
        }
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
     * <code>SalinasNode</code>, allowing the caller to avoid a cast.
     *
     * @param index the child index
     * @return a Salinas node
     */
    public SalinasNode getChild(int index) {
        return (SalinasNode) jjtGetChild(index);
    }
}
