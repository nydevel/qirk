package org.wrkr.clb.repo.mapper.security;

import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.user.UserMeta;

public class SecurityMemoWithProjectAndMembershipMapper extends SecurityMemoWithProjectMapper {

    public SecurityMemoWithProjectAndMembershipMapper(String memoTableName, ProjectMeta projectMeta,
            ProjectMemberMeta projectMemberMeta, UserMeta userMeta) {
        super(memoTableName);
        this.projectMapper = new SecurityProjectWithMembershipMapper(
                projectMeta, projectMemberMeta, userMeta);
    }
}
