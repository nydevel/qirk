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
package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.mapper.project.ProjectMemberMapper;

public class PublicUserWithProjectMembershipMapper extends PublicUserMapper {

    ProjectMemberMapper projectMemberMapper;

    public PublicUserWithProjectMembershipMapper(String userTableName, String projectMemberTableName) {
        super(userTableName);
        projectMemberMapper = new ProjectMemberMapper(projectMemberTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                projectMemberMapper.generateSelectColumnsStatement();
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = super.mapRow(rs, rowNum);
        if (rs.getObject(projectMemberMapper.generateColumnAlias(ProjectMemberMeta.id)) != null) {
            user.getProjectMembership().add(projectMemberMapper.mapRow(rs, rowNum));
        }
        return user;
    }

    @Override
    public User mapRow(Map<String, Object> result) {
        User user = super.mapRow(result);
        if (result.get(projectMemberMapper.generateColumnAlias(ProjectMemberMeta.id)) != null) {
            user.getProjectMembership().add(projectMemberMapper.mapRow(result));
        }
        return user;
    }

    public ProjectMember mapRowForProjectMember(Map<String, Object> result) {
        return projectMemberMapper.mapRow(result);
    }
}
