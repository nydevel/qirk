package org.wrkr.clb.repo.mapper.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.repo.mapper.BaseEntityMapper;

public class SecurityProjectMapper extends BaseEntityMapper<Project> {

    public SecurityProjectMapper() {
        super();
    }

    @Deprecated
    public SecurityProjectMapper(String tableName) {
        super(tableName);
    }

    public SecurityProjectMapper(ProjectMeta projectMeta) {
        super(projectMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(ProjectMeta.id) + ", " +
                generateSelectColumnStatement(ProjectMeta.isPrivate) + ", " +
                generateSelectColumnStatement(ProjectMeta.frozen);
    }

    @Override
    public Project mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Project project = new Project();

        project.setId(rs.getLong(generateColumnAlias(ProjectMeta.id)));
        project.setPrivate(rs.getBoolean(generateColumnAlias(ProjectMeta.isPrivate)));
        project.setFrozen(rs.getBoolean(generateColumnAlias(ProjectMeta.frozen)));

        return project;
    }
}
