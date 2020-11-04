package org.wrkr.clb.model;

public abstract class BaseEntityMeta {

    private String tableAlias = "";

    public BaseEntityMeta() {
    };

    public BaseEntityMeta(String tableAlias) {
        this.tableAlias = tableAlias;
    };

    public String getTableAlias() {
        return tableAlias;
    };
}
