package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.project.task.TaskHashtagMeta;

public class TaskHashtagMapper extends BaseMapper<TaskHashtag> {

    public TaskHashtagMapper() {
        super();
    }

    public TaskHashtagMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskHashtagMeta.id) + ", " +
                generateSelectColumnStatement(TaskHashtagMeta.projectId) + ", " +
                generateSelectColumnStatement(TaskHashtagMeta.name);
    }

    @Override
    public TaskHashtag mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        TaskHashtag hashtag = new TaskHashtag();

        hashtag.setId(rs.getLong(generateColumnAlias(TaskHashtagMeta.id)));
        hashtag.setProjectId(rs.getLong(generateColumnAlias(TaskHashtagMeta.projectId)));
        hashtag.setName(rs.getString(generateColumnAlias(TaskHashtagMeta.name)));

        return hashtag;
    }

    public TaskHashtag mapRow(Map<String, Object> result) {
        TaskHashtag hashtag = new TaskHashtag();

        hashtag.setId((Long) result.get(generateColumnAlias(TaskHashtagMeta.id)));
        hashtag.setProjectId((Long) result.get(generateColumnAlias(TaskHashtagMeta.projectId)));
        hashtag.setName((String) result.get(generateColumnAlias(TaskHashtagMeta.name)));

        return hashtag;
    }
}
