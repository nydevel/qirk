package org.wrkr.clb.model;

public abstract class BaseIdEntityMeta extends BaseEntityMeta {

    public static final String id = "id";

    public BaseIdEntityMeta() {
        super();
    };

    public BaseIdEntityMeta(String tableAlias) {
        super(tableAlias);
    };
}
