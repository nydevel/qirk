package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMeta;

public class ShortProjectMapper extends BaseMapper<Project> {

    public ShortProjectMapper() {
        super();
    }

    public ShortProjectMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(ProjectMeta.id) + ", " +
                generateSelectColumnStatement(ProjectMeta.name) + ", " +
                generateSelectColumnStatement(ProjectMeta.uiId) + ", " +
                generateSelectColumnStatement(ProjectMeta.isPrivate) + ", " +
                generateSelectColumnStatement(ProjectMeta.frozen);
    }

    @Override
    public Project mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Project project = new Project();

        project.setId(rs.getLong(generateColumnAlias(ProjectMeta.id)));
        project.setName(rs.getString(generateColumnAlias(ProjectMeta.name)));
        project.setUiId(rs.getString(generateColumnAlias(ProjectMeta.uiId)));
        project.setPrivate(rs.getBoolean(generateColumnAlias(ProjectMeta.isPrivate)));
        project.setFrozen(rs.getBoolean(generateColumnAlias(ProjectMeta.frozen)));

        return project;
    }
}
