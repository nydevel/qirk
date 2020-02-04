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
package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.user.UserFavorite;
import org.wrkr.clb.model.user.UserFavoriteMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.mapper.project.ShortProjectWithSecurityMembershipMapper;

public class UserFavoriteWithProjectAndOrgAndSecurityMembershipMapper extends BaseMapper<UserFavorite> {

    protected ShortProjectWithSecurityMembershipMapper projectMapper;

    public UserFavoriteWithProjectAndOrgAndSecurityMembershipMapper(String userFavoriteTableName,
            String projectTableName, ProjectMemberMeta projectMemberMeta, UserMeta userMeta) {
        super(userFavoriteTableName);
        this.projectMapper = new ShortProjectWithSecurityMembershipMapper(projectTableName, projectMemberMeta, userMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(UserFavoriteMeta.id) + ", " +
                generateSelectColumnStatement(UserFavoriteMeta.userId) + ", " +
                generateSelectColumnStatement(UserFavoriteMeta.projectId) + ", " +
                generateSelectColumnStatement(UserFavoriteMeta.previousId) + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public UserFavorite mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserFavorite favorite = new UserFavorite();

        favorite.setId(rs.getLong(generateColumnAlias(UserFavoriteMeta.id)));
        favorite.setUserId(rs.getLong(generateColumnAlias(UserFavoriteMeta.userId)));
        favorite.setProjectId((Long) rs.getObject(generateColumnAlias(UserFavoriteMeta.projectId))); // nullable
        favorite.setPreviousId((Long) rs.getObject(generateColumnAlias(UserFavoriteMeta.previousId))); // nullable

        if (rs.getObject(projectMapper.generateColumnAlias(ProjectMeta.id)) != null) {
            favorite.setProject(projectMapper.mapRow(rs, rowNum));
        }

        return favorite;
    }
}
