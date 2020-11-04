package org.wrkr.clb.model;

public abstract class BaseVersionedEntityMeta extends BaseIdEntityMeta {

    public static final String recordVersion = "record_version";

    public BaseVersionedEntityMeta() {
        super();
    };

    public BaseVersionedEntityMeta(String tableAlias) {
        super(tableAlias);
    };
}
