package com.idataconnect.salinas.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages multiple work areas in a script execution context.
 */
public class WorkAreaManager {
    private final Map<Integer, WorkArea> workAreas = new HashMap<>();
    private final Map<String, Integer> aliasToId = new HashMap<>();
    private int currentWorkArea = 1;

    public void use(int id, WorkArea workArea) throws IOException {
        WorkArea old = workAreas.get(id);
        if (old != null) {
            old.getDbf().close();
            aliasToId.remove(old.getAlias().toUpperCase());
        }
        workAreas.put(id, workArea);
        aliasToId.put(workArea.getAlias().toUpperCase(), id);
        currentWorkArea = id;
    }

    public Optional<WorkArea> getCurrentWorkArea() {
        return Optional.ofNullable(workAreas.get(currentWorkArea));
    }

    public Optional<WorkArea> getWorkArea(int id) {
        return Optional.ofNullable(workAreas.get(id));
    }

    public Optional<WorkArea> getWorkArea(String alias) {
        Integer id = aliasToId.get(alias.toUpperCase());
        if (id != null) {
            return getWorkArea(id);
        }
        return Optional.empty();
    }

    public int getCurrentWorkAreaId() {
        return currentWorkArea;
    }

    public void select(int id) {
        currentWorkArea = id;
    }

    public void closeAll() throws IOException {
        for (WorkArea wa : workAreas.values()) {
            wa.getDbf().close();
        }
        workAreas.clear();
        aliasToId.clear();
    }
}
