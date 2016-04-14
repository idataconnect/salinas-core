/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.function;

import com.idataconnect.salinas.SalinasException;

/**
 * Exception thrown when an error occurs while calling a function.
 */
public class FunctionCallException extends SalinasException {

    /**
     * Creates an empty function call exception.
     */
    public FunctionCallException() {
    }

    /**
     * Creates a function call exception with the given detail message.
     * @param message the detail message
     */
    public FunctionCallException(String message) {
        super(message);
    }

    /**
     * Creates a function call exception with the given detail message and
     * cause.
     * @param message the detail message
     * @param cause the cause
     */
    public FunctionCallException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a function call exception with the given cause.
     * @param cause the cause
     */
    public FunctionCallException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a function call exception with the given detail message,
     * filename, line number, and column number where the error occured.
     * @param message the detail message
     * @param filename the name of the file that was being interpreted when
     * the error occured
     * @param lineNumber the line number in the file that was being parsed
     * when the error occured
     * @param columnNumber the column number in the file that was being parsed
     * when the error occured
     */
    public FunctionCallException(String message, String filename, int lineNumber,
            int columnNumber) {
        super(message);
        setFilename(filename);
        setBeginLine(lineNumber);
        setBeginColumn(columnNumber);
    }

    /**
     * Creates a function call exception with the given detail message, cause,
     * filename, line number, and column number where the error occured.
     * @param message the detail message
     * @param cause the cause
     * @param filename the name of the file that was being interpreted when
     * the error occured
     * @param lineNumber the line number in the file that was being parsed
     * when the error occured
     * @param columnNumber the column number in the file that was being parsed
     * when the error occured
     */
    public FunctionCallException(String message, Throwable cause, String filename,
            int lineNumber, int columnNumber) {
        super(message, cause);
        setFilename(filename);
        setBeginLine(lineNumber);
        setBeginColumn(columnNumber);
    }

    /**
     * Creates a function call exception with the given detail cause,
     * filename, line number, and column number where the error occured.
     * @param cause the cause
     * @param filename the name of the file that was being interpreted when
     * the error occured
     * @param lineNumber the line number in the file that was being parsed
     * when the error occured
     * @param columnNumber the column number in the file that was being parsed
     * when the error occured
     */
    public FunctionCallException(Throwable cause, String filename, int lineNumber,
            int columnNumber) {
        super(cause);
        setFilename(filename);
        setBeginLine(lineNumber);
        setBeginColumn(columnNumber);
    }
}
