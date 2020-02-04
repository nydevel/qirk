/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.ProjectMemberMeta;

public class ProjectMemberMapper extends BaseMapper<ProjectMember> {

    public ProjectMemberMapper() {
        super();
    }

    public ProjectMemberMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(ProjectMemberMeta.id) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.userId) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.organizationMemberId) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.projectId) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.writeAllowed) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.manager) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.hiredAt) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.firedAt) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.fired);
    }

    @Override
    public ProjectMember mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        ProjectMember member = new ProjectMember();

        member.setId(rs.getLong(generateColumnAlias(ProjectMemberMeta.id)));
        member.setUserId(rs.getLong(generateColumnAlias(ProjectMemberMeta.userId)));
        member.setProjectId(rs.getLong(generateColumnAlias(ProjectMemberMeta.projectId)));
        member.setWriteAllowed(rs.getBoolean(generateColumnAlias(ProjectMemberMeta.writeAllowed)));
        member.setManager(rs.getBoolean(generateColumnAlias(ProjectMemberMeta.manager)));
        member.setHiredAt(getOffsetDateTime(rs, generateColumnAlias(ProjectMemberMeta.hiredAt)));
        member.setFiredAt(getOffsetDateTime(rs, generateColumnAlias(ProjectMemberMeta.firedAt)));
        member.setFired(rs.getBoolean(generateColumnAlias(ProjectMemberMeta.fired)));

        return member;
    }
}
