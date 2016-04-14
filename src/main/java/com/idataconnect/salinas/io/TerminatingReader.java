/*
 * Copyright (c) 2011-2012 i Data Connect!
 */
package com.idataconnect.salinas.io;

import java.io.IOException;
import java.io.Reader;

/**
 * A {@link Reader} implementation which decorates another <code>Reader</code>
 * instance, and appends a newline to the contents read from the decorated
 * reader, if a newline was not already the final character.
 * This is used to ensure that the last statement in a script is terminated.
 */
public class TerminatingReader extends Reader {
    
    private final Reader wrappedReader;

    private boolean eof = false;
    private char lastChar;

    public TerminatingReader(Reader wrappedReader) {
        this.wrappedReader = wrappedReader;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (eof) {
            return -1;
        } else {
            int read = wrappedReader.read(cbuf, off, len);
            if (read == -1) {
                eof = true;
                if (lastChar != '\r' && lastChar != '\n') {
                    cbuf[off] = '\n';
                    return 1;
                } else {
                    return -1;
                }
            } else {
                if (read > 0) {
                    lastChar = cbuf[off + (read - 1)];
                }
                return read;
            }
        }
    }

    @Override
    public void close() throws IOException {
        wrappedReader.close();
    }
}