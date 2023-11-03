/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.parser.SalinasNode;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * The function call stack implementation used by Salinas.
 */
public class CallStack {

    private final Deque<StackFrame> stack = new LinkedList<>();

    /**
     * Pushes a stack frame onto the call stack.
     * @param node the node which is calling another function
     * @return the call stack instance
     */
    public CallStack push(SalinasNode node) {
        final StackFrame frame = new StackFrame(node);
        stack.push(frame);
        return this;
    }

    /**
     * Pops a stack frame from the call stack.
     * @return the frame that was popped
     */
    public StackFrame pop() {
        return stack.pop();
    }

    /**
     * Prints the stack trace to <code>System.err</code>.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints the stack trace to the given print stream.
     * @param out the print stream to print the stack trace to.
     */
    public void printStackTrace(PrintStream out) {
        for (StackFrame frame : stack) {
            out.println("\tat " + frame.getNode().getFilename() + ":"
                    + frame.getNode().getBeginLine());
        }
    }

    /**
     * Gets elements of the call stack.
     *
     * @return a list of stack frames
     */
    public List<StackFrame> getStackTrace() {
        return Collections.unmodifiableList((LinkedList) stack);
    }
}
