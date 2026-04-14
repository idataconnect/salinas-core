package com.idataconnect.salinas;

/**
 * Interface for UI-related interactions requested by the core engine.
 */
public interface SalinasUI {
    /**
     * Opens a browse window for the given alias.
     * @param alias the work area alias
     */
    void browse(String alias);
    
    /**
     * Shows a message box.
     * @param title the title
     * @param message the message
     */
    void messageBoxPopup(String title, String message);

    /**
     * Prints text at the given row and column coordinate.
     * @param row the row (0-indexed)
     * @param col the column (0-indexed)
     * @param text the text to print
     */
    void say(int row, int col, String text);

    /**
     * Sets the current working directory path in the UI.
     * @param path the absolute path
     */
    void setCurrentPath(String path);
}
