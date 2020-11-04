package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.repo.mapper.BaseEntityMapper;

public class ProjectMemberMapper extends BaseEntityMapper<ProjectMember> {

    public ProjectMemberMapper() {
        super();
    }

    public ProjectMemberMapper(String tableName) {
        super(tableName);
    }

    public ProjectMemberMapper(ProjectMemberMeta projectMemberMeta) {
        super(projectMemberMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(ProjectMemberMeta.id) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.userId) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.projectId) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.writeAllowed) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.manager) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.hiredAt) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.firedAt) + ", " +
                generateSelectColumnStatement(ProjectMemberMeta.fired);
    }

    @Override
    public ProjectMember mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        ProjectMember member = new ProjectMember();

        member.setId(rs.getLong(generateColumnAlias(ProjectMemberMeta.id)));
        member.setUserId(rs.getLong(generateColumnAlias(ProjectMemberMeta.userId)));
        member.setProjectId(rs.getLong(generateColumnAlias(ProjectMemberMeta.projectId)));
        member.setWriteAllowed(rs.getBoolean(generateColumnAlias(ProjectMemberMeta.writeAllowed)));
        member.setManager(rs.getBoolean(generateColumnAlias(ProjectMemberMeta.manager)));
        member.setHiredAt(getOffsetDateTime(rs, generateColumnAlias(ProjectMemberMeta.hiredAt)));
        member.setFiredAt(getOffsetDateTime(rs, generateColumnAlias(ProjectMemberMeta.firedAt)));
        member.setFired(rs.getBoolean(generateColumnAlias(ProjectMemberMeta.fired)));

        return member;
    }

    public ProjectMember mapRow(Map<String, Object> result) {
        ProjectMember member = new ProjectMember();

        member.setId((Long) result.get(generateColumnAlias(ProjectMemberMeta.id)));
        member.setUserId((Long) result.get(generateColumnAlias(ProjectMemberMeta.userId)));
        member.setProjectId((Long) result.get(generateColumnAlias(ProjectMemberMeta.projectId)));
        member.setWriteAllowed((boolean) result.get(generateColumnAlias(ProjectMemberMeta.writeAllowed)));
        member.setManager((boolean) result.get(generateColumnAlias(ProjectMemberMeta.manager)));
        member.setHiredAt(getOffsetDateTime(result, generateColumnAlias(ProjectMemberMeta.hiredAt)));
        member.setFiredAt(getOffsetDateTime(result, generateColumnAlias(ProjectMemberMeta.firedAt)));
        member.setFired((boolean) result.get(generateColumnAlias(ProjectMemberMeta.fired)));

        return member;
    }
}
