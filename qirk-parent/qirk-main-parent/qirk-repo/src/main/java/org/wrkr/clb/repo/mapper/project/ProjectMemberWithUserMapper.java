package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.mapper.user.BaseUserMapper;

public class ProjectMemberWithUserMapper extends ProjectMemberMapper {

    private BaseUserMapper userMapper;

    public ProjectMemberWithUserMapper(ProjectMemberMeta projectMemberMeta, UserMeta userMeta) {
        super(projectMemberMeta);
        this.userMapper = new BaseUserMapper(userMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                userMapper.generateSelectColumnsStatement();
    }

    @Override
    public ProjectMember mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProjectMember member = super.mapRow(rs, rowNum);
        member.setUser(userMapper.mapRow(rs, rowNum));
        return member;
    }
}
