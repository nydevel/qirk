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
