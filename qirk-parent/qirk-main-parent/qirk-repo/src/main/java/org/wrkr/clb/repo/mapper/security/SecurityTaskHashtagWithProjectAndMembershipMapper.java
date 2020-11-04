package org.wrkr.clb.repo.mapper.security;

import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.user.UserMeta;

public class SecurityTaskHashtagWithProjectAndMembershipMapper extends SecurityTaskHashtagWithProjectMapper {

    public SecurityTaskHashtagWithProjectAndMembershipMapper(String hashtagTableName, ProjectMeta projectMeta,
            ProjectMemberMeta projectMemberMeta, UserMeta userMeta) {
        super(hashtagTableName);
        this.projectMapper = new SecurityProjectWithMembershipMapper(
                projectMeta, projectMemberMeta, userMeta);
    }
}
