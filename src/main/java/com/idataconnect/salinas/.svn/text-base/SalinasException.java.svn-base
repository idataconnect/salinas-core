/*
 * Copyright 2011-2013 i Data Connect!
 */
package com.idataconnect.salinas;

/**
 * Base exception for internal Salinas errors during parsing.
 */
public class SalinasException extends Exception {

    private static final long serialVersionUID = 1L;

    private String filename;
    private int beginLine;
    private int beginColumn;
    
    /**
     * Creates a new Salinas Exception with the given cause.
     *
     * @param cause the cause
     */
    public SalinasException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new Salinas Exception with the given message and cause.
     *
     * @param message the message
     * @param cause the cause
     */
    public SalinasException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new Salinas Exception with the given detail message.
     *
     * @param message the detail message
     */
    public SalinasException(String message) {
        super(message);
    }

    /**
     * Creates a new Salinas Exception with the given detail message, and the
     * location of the error.
     *
     * @param message the detail message
     * @param filename the filename that contains the script being executed
     * @param lineNumber the line number within the file, where the error occured
     * @param columnNumber the column number within the file, where the error
     * occured
     */
    public SalinasException(String message, String filename, int lineNumber,
            int columnNumber) {
        super(message);
        setFilename(filename);
        setBeginLine(lineNumber);
        setBeginColumn(columnNumber);
    }

    /**
     * Creates a new Salinas Exception with the given detail message and
     * cause, and the location of the error.
     *
     * @param message the detail message
     * @param cause the cause
     * @param filename the filename that contains the script being executed
     * @param lineNumber the line number within the file, where the error occured
     * @param columnNumber the column number within the file, where the error
     * occured
     */
    public SalinasException(String message, Throwable cause, String filename,
            int lineNumber, int columnNumber) {
        super(message, cause);
        setFilename(filename);
        setBeginLine(lineNumber);
        setBeginColumn(columnNumber);
    }

    /**
     * Creates a new Salinas Exception with the given cause, and the location
     * of the error.
     *
     * @param cause the cause
     * @param filename the filename that contains the script being executed
     * @param lineNumber the line number within the file, where the error occured
     * @param columnNumber the column number within the file, where the error
     * occured
     */
    public SalinasException(Throwable cause, String filename, int lineNumber,
            int columnNumber) {
        super(cause);
        setFilename(filename);
        setBeginLine(lineNumber);
        setBeginColumn(columnNumber);
    }

    /**
     * Creates a new Salinas Exception without a message or cause.
     */
    public SalinasException() {
    }

    /**
     * Gets the column where the error occured, or <code>-1</code> if the
     * column is not known.
     */
    public int getBeginColumn() {
        return beginColumn;
    }

    /**
     * Sets the column where the error occured.
     * @param beginColumn the column
     */
    public void setBeginColumn(int beginColumn) {
        this.beginColumn = beginColumn;
    }

    /**
     * Sets the line where the error occured, or <code>-1</code> if the line
     * is not known.
     * @return the line
     */
    public int getBeginLine() {
        return beginLine;
    }

    /**
     * Sets the line where the error occured.
     * @param beginLine the line
     */
    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    /**
     * Gets the filename where the error occured, or <code>null</code> if the
     * filename is not known. Note that the filename should be be assumed to
     * represent a file within the local system.
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename where the error occured.
     * @param filename the filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
