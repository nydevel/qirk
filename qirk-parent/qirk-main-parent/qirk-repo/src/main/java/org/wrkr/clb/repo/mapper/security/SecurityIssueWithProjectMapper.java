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
package org.wrkr.clb.repo.mapper.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.IssueMeta;

public class SecurityIssueWithProjectMapper extends BaseMapper<Issue> {

    protected SecurityProjectMapper projectMapper;

    protected SecurityIssueWithProjectMapper(String issueTableName) {
        super(issueTableName);
    }

    public SecurityIssueWithProjectMapper(String issueTableName, String projectTableName) {
        super(issueTableName);
        this.projectMapper = new SecurityProjectMapper(projectTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(IssueMeta.id) + ", " +
                generateSelectColumnStatement(IssueMeta.projectId) + ", " +
                generateSelectColumnStatement(IssueMeta.reporterId) + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public Issue mapRow(ResultSet rs, int rowNum) throws SQLException {
        Issue issue = new Issue();

        issue.setId(rs.getLong(generateColumnAlias(IssueMeta.id)));
        issue.setProjectId(rs.getLong(generateColumnAlias(IssueMeta.projectId)));
        issue.setReporterId((Long) rs.getObject(generateColumnAlias(IssueMeta.reporterId))); // nullable

        issue.setProject(projectMapper.mapRow(rs, rowNum));

        return issue;
    }
}
