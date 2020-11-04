package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.repo.mapper.security.SecurityProjectMapper;

public class ProjectMemberWithProjectMapper extends ProjectMemberMapper {

    private SecurityProjectMapper projectMapper;

    public ProjectMemberWithProjectMapper(String projectMemberTableName, String projectTableName) {
        super(projectMemberTableName);
        this.projectMapper = new SecurityProjectMapper(projectTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public ProjectMember mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProjectMember member = super.mapRow(rs, rowNum);
        member.setProject(projectMapper.mapRow(rs, rowNum));
        return member;
    }
}
