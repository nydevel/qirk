package org.wrkr.clb.repo.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.VersionedIdEntity;
import org.wrkr.clb.model.BaseVersionedEntityMeta;

public abstract class VersionedEntityMapper<E extends VersionedIdEntity> extends BaseMapper<E> {

    public VersionedEntityMapper() {
        super();
    }

    public VersionedEntityMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(BaseVersionedEntityMeta.id) + ", " +
                generateSelectColumnStatement(BaseVersionedEntityMeta.recordVersion);
    }

    @SuppressWarnings("unused")
    @Override
    public E mapRow(ResultSet rs, int rowNum) throws SQLException {
        throw new UnsupportedOperationException();
    }
}
