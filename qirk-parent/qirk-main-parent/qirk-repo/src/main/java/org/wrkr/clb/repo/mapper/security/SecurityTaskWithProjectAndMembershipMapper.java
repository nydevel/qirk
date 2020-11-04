package org.wrkr.clb.repo.mapper.security;

import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.user.UserMeta;

public class SecurityTaskWithProjectAndMembershipMapper extends SecurityTaskWithProjectMapper {

    public SecurityTaskWithProjectAndMembershipMapper(String taskTableName, ProjectMeta projectMeta,
            ProjectMemberMeta projectMemberMeta, UserMeta userMeta) {
        super(taskTableName);
        this.projectMapper = new SecurityProjectWithMembershipMapper(
                projectMeta, projectMemberMeta, userMeta);
    }
}
