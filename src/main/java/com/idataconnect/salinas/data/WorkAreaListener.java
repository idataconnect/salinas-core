package com.idataconnect.salinas.data;

/**
 * Interface for components that need to be notified when the database environment changes.
 */
public interface WorkAreaListener {

    /**
     * Called when a database is opened or changed in a specific work area.
     * @param id The work area ID (1-10).
     * @param workArea The new WorkArea object, or null if it was closed.
     */
    void onWorkAreaChanged(int id, WorkArea workArea);

    /**
     * Called when the current active work area is selected.
     * @param id The new active work area ID.
     */
    void onCurrentWorkAreaChanged(int id);

    /**
     * Called when all databases are closed simultaneously.
     */
    void onAllClosed();
}
