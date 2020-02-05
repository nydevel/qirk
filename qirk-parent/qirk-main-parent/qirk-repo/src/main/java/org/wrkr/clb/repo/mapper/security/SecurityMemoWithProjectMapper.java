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
package org.wrkr.clb.repo.mapper.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.MemoMeta;
import org.wrkr.clb.model.project.ProjectMeta;

public class SecurityMemoWithProjectMapper extends BaseMapper<Memo> {

    protected SecurityProjectMapper projectMapper;

    protected SecurityMemoWithProjectMapper(String memoTableName) {
        super(memoTableName);
    }

    public SecurityMemoWithProjectMapper(String memoTableName, ProjectMeta projectMeta) {
        super(memoTableName);
        this.projectMapper = new SecurityProjectMapper(projectMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(MemoMeta.id) + ", " +
                generateSelectColumnStatement(MemoMeta.projectId) + ", " +
                generateSelectColumnStatement(MemoMeta.authorId) + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public Memo mapRow(ResultSet rs, int rowNum) throws SQLException {
        Memo memo = new Memo();

        memo.setId(rs.getLong(generateColumnAlias(MemoMeta.id)));
        memo.setProjectId(rs.getLong(generateColumnAlias(MemoMeta.projectId)));
        memo.setAuthorId(rs.getLong(generateColumnAlias(MemoMeta.authorId)));

        memo.setProject(projectMapper.mapRow(rs, rowNum));

        return memo;
    }
}
