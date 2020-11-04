package org.wrkr.clb.repo.mapper.security;

import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.user.UserMeta;

public class SecurityIssueWithProjectAndMembershipMapper extends SecurityIssueWithProjectMapper {

    public SecurityIssueWithProjectAndMembershipMapper(String issueTableName, ProjectMeta projectMeta,
            ProjectMemberMeta projectMemberMeta, UserMeta userMeta) {
        super(issueTableName);
        this.projectMapper = new SecurityProjectWithMembershipMapper(
                projectMeta, projectMemberMeta, userMeta);
    }
}
