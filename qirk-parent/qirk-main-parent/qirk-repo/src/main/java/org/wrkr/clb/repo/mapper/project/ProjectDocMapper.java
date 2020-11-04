package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMeta;

public class ProjectDocMapper extends BaseMapper<Project> {

    public ProjectDocMapper() {
        super();
    }

    public ProjectDocMapper(String projectTableName) {
        super(projectTableName);

    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(ProjectMeta.id) + ", " +
                generateSelectColumnStatement(ProjectMeta.recordVersion) + ", " +
                generateSelectColumnStatement(ProjectMeta.documentationHtml) + ", " +
                generateSelectColumnStatement(ProjectMeta.documentationMd);
    }

    @Override
    public Project mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Project project = new Project();

        project.setId(rs.getLong(generateColumnAlias(ProjectMeta.id)));
        project.setRecordVersion(rs.getLong(generateColumnAlias(ProjectMeta.recordVersion)));
        project.setDocumentationHtml(rs.getString(generateColumnAlias(ProjectMeta.documentationHtml)));
        project.setDocumentationMd(rs.getString(generateColumnAlias(ProjectMeta.documentationMd)));

        return project;
    }
}
