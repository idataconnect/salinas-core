package com.idataconnect.salinas.data;

import com.idataconnect.jdbfdriver.DBF;

/**
 * A work area which holds a DBF instance and its alias.
 */
public class WorkArea {
    private final String alias;
    private final DBF dbf;

    public WorkArea(String alias, DBF dbf) {
        this.alias = alias;
        this.dbf = dbf;
    }

    public String getAlias() {
        return alias;
    }

    public DBF getDbf() {
        return dbf;
    }
}
