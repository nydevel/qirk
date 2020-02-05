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
package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.user.UserMeta;

public class ProjectWithEverythingForReadAndSecurityMembershipMapper extends ProjectWithEverythingForReadMapper {

    private ProjectMemberWithUserMapper projectMemberMapper;

    public ProjectWithEverythingForReadAndSecurityMembershipMapper(String projectTableName,
            ProjectMemberMeta projectMemberMeta, UserMeta userMeta) {
        super(projectTableName);
        this.projectMemberMapper = new ProjectMemberWithUserMapper(projectMemberMeta, userMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                projectMemberMapper.generateSelectColumnsStatement();
    }

    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = super.mapRow(rs, rowNum);

        if (rs.getObject(projectMemberMapper.generateColumnAlias(ProjectMemberMeta.id)) != null) {
            project.getMembers().add(projectMemberMapper.mapRow(rs, rowNum));
        }

        return project;
    }
}
