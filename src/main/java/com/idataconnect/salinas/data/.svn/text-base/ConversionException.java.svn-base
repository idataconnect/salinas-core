/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas.data;

import com.idataconnect.salinas.SalinasException;

/**
 * An exception thrown when one Salinas type could not be converted to another.
 * An example of this would be an attempt to convert a function to a number.
 */
public class ConversionException extends SalinasException {

    /**
     * Creates a new conversion exception without a detail message or cause.
     */
    public ConversionException() {
    }

    /**
     * Creates a new conversion exception with the given detail message.
     * @param message the detail message
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * Creates a new conversion exception with the given detail message
     * and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new conversion exception with the given cause.
     * @param cause the cause
     */
    public ConversionException(Throwable cause) {
        super(cause);
    }

    public ConversionException(Throwable cause, String filename, int lineNumber,
            int columnNumber) {
        super(cause, filename, lineNumber, columnNumber);
    }

    public ConversionException(String message, Throwable cause, String filename,
            int lineNumber, int columnNumber) {
        super(message, cause, filename, lineNumber, columnNumber);
    }

    public ConversionException(String message, String filename, int lineNumber,
            int columnNumber) {
        super(message, filename, lineNumber, columnNumber);
    }
}
