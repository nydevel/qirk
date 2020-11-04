package org.wrkr.clb.repo.mapper.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.IssueMeta;
import org.wrkr.clb.model.project.ProjectMeta;

public class SecurityIssueWithProjectMapper extends BaseMapper<Issue> {

    protected SecurityProjectMapper projectMapper;

    protected SecurityIssueWithProjectMapper(String issueTableName) {
        super(issueTableName);
    }

    public SecurityIssueWithProjectMapper(String issueTableName, ProjectMeta projectMeta) {
        super(issueTableName);
        this.projectMapper = new SecurityProjectMapper(projectMeta);
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
