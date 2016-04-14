/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.parser.SalinasNode;

/**
 * A stack frame contained by the call stack.
 */
public class StackFrame {

    private SalinasNode node;

    /**
     * Creates a new empty stack frame instance.
     */
    public StackFrame() {
    }

    /**
     * Creates a stack frame instance with the node set.
     * @param node the node which is calling a function
     */
    public StackFrame(SalinasNode node) {
        this.node = node;
    }

    /**
     * Gets the stack frame node.
     * @return the node which called a function
     */
    public SalinasNode getNode() {
        return node;
    }

    /**
     * Sets the node which is calling a function
     * @param node the node which is calling a function
     */
    public void setNode(SalinasNode node) {
        this.node = node;
    }
}