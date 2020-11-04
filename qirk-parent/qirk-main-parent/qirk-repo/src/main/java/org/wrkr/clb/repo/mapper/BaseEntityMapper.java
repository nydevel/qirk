package org.wrkr.clb.repo.mapper;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.BaseEntity;
import org.wrkr.clb.model.BaseEntityMeta;

public abstract class BaseEntityMapper<E extends BaseEntity> extends BaseMapper<E> {

    public BaseEntityMapper() {
    }

    @Deprecated
    public BaseEntityMapper(String tableName) {
        super(tableName);
    }

    public BaseEntityMapper(BaseEntityMeta entityMeta) {
        super(entityMeta.getTableAlias());
    }
}
