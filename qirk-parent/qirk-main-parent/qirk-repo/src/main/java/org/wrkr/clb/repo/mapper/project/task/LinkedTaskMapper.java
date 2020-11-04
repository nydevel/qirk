package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.mapper.project.ProjectMemberWithUserMapper;

public class LinkedTaskMapper extends ShortTaskMapper {

    private ProjectMemberWithUserMapper assigneeMapper;
    private TaskStatusMapper statusMapper;

    public LinkedTaskMapper(String taskTableName, String statusTableName,
            ProjectMemberMeta projectMemberMeta, UserMeta userMeta) {
        super(taskTableName);
        assigneeMapper = new ProjectMemberWithUserMapper(projectMemberMeta, userMeta);
        statusMapper = new TaskStatusMapper(statusTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                assigneeMapper.generateSelectColumnsStatement() + ", " +
                statusMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = super.mapRow(rs, rowNum);

        if (rs.getObject(assigneeMapper.generateColumnAlias(ProjectMemberMeta.id)) != null) {
            task.setAssignee(assigneeMapper.mapRow(rs, rowNum));
        }
        task.setStatus(statusMapper.mapRow(rs, rowNum));

        return task;
    }
}
