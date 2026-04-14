package com.idataconnect.salinas.parser;

/**
 * Encapsulates options for an INDEX command.
 */
public class IndexOptions {
    private String tagName;
    private String filename;
    private boolean unique;
    private boolean descending;
    private String expressionSource;

    public IndexOptions() {
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public String getExpressionSource() {
        return expressionSource;
    }

    public void setExpressionSource(String expressionSource) {
        this.expressionSource = expressionSource;
    }
}
