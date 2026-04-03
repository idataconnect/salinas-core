/*
 * Copyright (c) 2011-2016 i Data Connect!
 */
package com.idataconnect.salinas.parser;

/**
 * Base class of all Salinas nodes.
 */
public class SalinasNode extends SimpleNode {

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
