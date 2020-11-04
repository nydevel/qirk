package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMeta;

public class ProjectWithEverythingForReadMapper extends ProjectNameAndUiIdMapper {

    public ProjectWithEverythingForReadMapper(String projectTableName) {
        super(projectTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(ProjectMeta.recordVersion) + ", " +
                generateSelectColumnStatement(ProjectMeta.key) + ", " +
                generateSelectColumnStatement(ProjectMeta.isPrivate) + ", " +
                generateSelectColumnStatement(ProjectMeta.descriptionHtml) + ", " +
                generateSelectColumnStatement(ProjectMeta.descriptionMd);
    }

    @Override
    public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = super.mapRow(rs, rowNum);

        project.setRecordVersion(rs.getLong(generateColumnAlias(ProjectMeta.recordVersion)));
        project.setKey(rs.getString(generateColumnAlias(ProjectMeta.key)));
        project.setPrivate(rs.getBoolean(generateColumnAlias(ProjectMeta.isPrivate)));
        project.setDescriptionHtml(rs.getString(generateColumnAlias(ProjectMeta.descriptionHtml)));
        project.setDescriptionMd(rs.getString(generateColumnAlias(ProjectMeta.descriptionMd)));

        return project;
    }
}
