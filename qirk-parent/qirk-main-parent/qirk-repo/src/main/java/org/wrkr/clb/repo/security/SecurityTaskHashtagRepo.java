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
package org.wrkr.clb.repo.security;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskHashtagMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.security.SecurityTaskHashtagWithProjectAndMembershipMapper;
import org.wrkr.clb.repo.mapper.security.SecurityTaskHashtagWithProjectMapper;

@Repository
public class SecurityTaskHashtagRepo extends JDBCBaseMainRepo {

    private static final SecurityTaskHashtagWithProjectMapper SECURITY_TASK_HASHTAG_MAPPER = new SecurityTaskHashtagWithProjectMapper(
            TaskHashtagMeta.TABLE_NAME, ProjectMeta.DEFAULT);

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT_FOR_SECURITY = "SELECT " +
            SECURITY_TASK_HASHTAG_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.id + " = ?;"; // 1

    private static final SecurityTaskHashtagWithProjectAndMembershipMapper SECURITY_TASK_HASHTAG_WITH_MEMBERSHIP_MAPPER = new SecurityTaskHashtagWithProjectAndMembershipMapper(
            TaskHashtagMeta.TABLE_NAME, ProjectMeta.DEFAULT, ProjectMemberMeta.DEFAULT, UserMeta.DEFAULT);

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY = "SELECT "
            + SECURITY_TASK_HASHTAG_WITH_MEMBERSHIP_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskHashtagMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            SecurityProjectRepo.JOIN_NOT_FIRED_PROJECT_MEMBER_AND_USER_ON_PROJECT_ID_AND_USER_ID + " " +
            "WHERE " + TaskHashtagMeta.TABLE_NAME + "." + TaskHashtagMeta.id + " = ?;"; // 2

    public TaskHashtag getByIdAndFetchProjectForSecurity(Long hashtagId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT_FOR_SECURITY, SECURITY_TASK_HASHTAG_MAPPER,
                hashtagId);
    }

    public TaskHashtag getByIdAndFetchProjectAndMembershipByUserIdForSecurity(Long hashtagId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY,
                SECURITY_TASK_HASHTAG_WITH_MEMBERSHIP_MAPPER,
                userId, hashtagId);
    }
}
