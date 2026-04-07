package com.idataconnect.salinas.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages multiple work areas in a script execution context.
 */
public class WorkAreaManager {
    private final Map<Integer, WorkArea> workAreas = new HashMap<>();
    private final Map<String, Integer> aliasToId = new HashMap<>();
    private final List<WorkAreaListener> listeners = new ArrayList<>();
    private int currentWorkArea = 1;

    public void addListener(WorkAreaListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WorkAreaListener listener) {
        listeners.remove(listener);
    }

    private void notifyWorkAreaChanged(int id, WorkArea wa) {
        for (WorkAreaListener l : new ArrayList<>(listeners)) {
            l.onWorkAreaChanged(id, wa);
        }
    }

    private void notifyCurrentAreaChanged(int id) {
        for (WorkAreaListener l : new ArrayList<>(listeners)) {
            l.onCurrentWorkAreaChanged(id);
        }
    }

    private void notifyAllClosed() {
        for (WorkAreaListener l : new ArrayList<>(listeners)) {
            l.onAllClosed();
        }
    }

    public void use(int id, WorkArea workArea) throws IOException {
        WorkArea old = workAreas.get(id);
        if (old != null) {
            old.getDbf().close();
            aliasToId.remove(old.getAlias().toUpperCase());
        }
        if (workArea != null) {
            workAreas.put(id, workArea);
            aliasToId.put(workArea.getAlias().toUpperCase(), id);
        } else {
            workAreas.remove(id);
        }
        currentWorkArea = id;
        notifyWorkAreaChanged(id, workArea);
        notifyCurrentAreaChanged(id);
    }

    public Optional<WorkArea> getCurrentWorkArea() {
        return Optional.ofNullable(workAreas.get(currentWorkArea));
    }

    public Optional<WorkArea> getWorkArea(int id) {
        return Optional.ofNullable(workAreas.get(id));
    }

    public Optional<WorkArea> getWorkArea(String alias) {
        return Optional.ofNullable(alias)
            .map(String::toUpperCase)
            .map(aliasToId::get)
            .flatMap(this::getWorkArea);
    }

    public int getCurrentWorkAreaId() {
        return currentWorkArea;
    }

    public void select(int id) {
        currentWorkArea = id;
        notifyCurrentAreaChanged(id);
    }

    public void closeAll() throws IOException {
        IOException lastEx = null;
        for (WorkArea wa : workAreas.values()) {
            try {
                wa.getDbf().close();
            } catch (IOException ex) {
                lastEx = ex;
            }
        }
        workAreas.clear();
        aliasToId.clear();
        notifyAllClosed();
        if (lastEx != null) {
            throw lastEx;
        }
    }
}
