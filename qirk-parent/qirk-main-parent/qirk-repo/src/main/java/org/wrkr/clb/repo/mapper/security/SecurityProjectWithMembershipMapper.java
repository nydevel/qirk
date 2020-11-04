package org.wrkr.clb.repo.mapper.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.mapper.project.ProjectMemberWithUserMapper;

public class SecurityProjectWithMembershipMapper extends SecurityProjectMapper {

    private ProjectMemberWithUserMapper projectMemberMapper;

    public SecurityProjectWithMembershipMapper(ProjectMeta projectMeta, ProjectMemberMeta projectMemberMeta, UserMeta userMeta) {
        super(projectMeta);
        this.projectMemberMapper = new ProjectMemberWithUserMapper(projectMemberMeta, userMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " + projectMemberMapper.generateSelectColumnsStatement();
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
