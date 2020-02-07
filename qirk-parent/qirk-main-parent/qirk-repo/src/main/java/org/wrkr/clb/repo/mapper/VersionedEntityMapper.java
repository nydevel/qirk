/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
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
