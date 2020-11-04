package org.wrkr.clb.repo.mapper.security;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.repo.mapper.project.task.TaskHashtagMapper;

public class SecurityTaskHashtagWithProjectMapper extends TaskHashtagMapper {

    protected SecurityProjectMapper projectMapper;

    protected SecurityTaskHashtagWithProjectMapper(String hashtagTableName) {
        super(hashtagTableName);
    }

    public SecurityTaskHashtagWithProjectMapper(String hashtagTableName, ProjectMeta projectMeta) {
        super(hashtagTableName);
        this.projectMapper = new SecurityProjectMapper(projectMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public TaskHashtag mapRow(ResultSet rs, int rowNum) throws SQLException {
        TaskHashtag hashtag = super.mapRow(rs, rowNum);
        hashtag.setProject(projectMapper.mapRow(rs, rowNum));
        return hashtag;
    }
}
