package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.mapper.project.ProjectMemberWithUserMapper;
import org.wrkr.clb.repo.mapper.project.ShortProjectMapper;

public class TaskWithEverythingForReadMapper extends BaseMapper<Task> {

    private ShortProjectMapper projectMapper;

    private ProjectMemberWithUserMapper reporterMapper;
    private ProjectMemberWithUserMapper assigneeMapper;

    private TaskTypeMapper typeMapper;
    private TaskPriorityMapper priorityMapper;
    private TaskStatusMapper statusMapper;

    public TaskWithEverythingForReadMapper(String taskTableName, String projectTableName,
            ProjectMemberMeta reporterMemberMeta, UserMeta reporterUserMeta,
            ProjectMemberMeta assigneeMemberMeta, UserMeta assigneeUserMeta,
            String typeTableName, String priorityTableName, String statusTableName) {
        super(taskTableName);
        projectMapper = new ShortProjectMapper(projectTableName);
        reporterMapper = new ProjectMemberWithUserMapper(reporterMemberMeta, reporterUserMeta);
        assigneeMapper = new ProjectMemberWithUserMapper(assigneeMemberMeta, assigneeUserMeta);
        typeMapper = new TaskTypeMapper(typeTableName);
        priorityMapper = new TaskPriorityMapper(priorityTableName);
        statusMapper = new TaskStatusMapper(statusTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TaskMeta.id) + ", " +
                generateSelectColumnStatement(TaskMeta.number) + ", " +
                generateSelectColumnStatement(TaskMeta.summary) + ", " +
                generateSelectColumnStatement(TaskMeta.recordVersion) + ", " +
                generateSelectColumnStatement(TaskMeta.descriptionMd) + ", " +
                generateSelectColumnStatement(TaskMeta.descriptionHtml) + ", " +
                generateSelectColumnStatement(TaskMeta.createdAt) + ", " +
                generateSelectColumnStatement(TaskMeta.updatedAt) + ", " +
                projectMapper.generateSelectColumnsStatement() + ", " +
                reporterMapper.generateSelectColumnsStatement() + ", " +
                assigneeMapper.generateSelectColumnsStatement() + ", " +
                typeMapper.generateSelectColumnsStatement() + ", " +
                priorityMapper.generateSelectColumnsStatement() + ", " +
                statusMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = new Task();

        task.setId(rs.getLong(generateColumnAlias(TaskMeta.id)));
        task.setNumber(rs.getLong(generateColumnAlias(TaskMeta.number)));
        task.setSummary(rs.getString(generateColumnAlias(TaskMeta.summary)));
        task.setRecordVersion(rs.getLong(generateColumnAlias(TaskMeta.recordVersion)));
        task.setDescriptionMd(rs.getString(generateColumnAlias(TaskMeta.descriptionMd)));
        task.setDescriptionHtml(rs.getString(generateColumnAlias(TaskMeta.descriptionHtml)));
        task.setCreatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskMeta.createdAt)));
        task.setUpdatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskMeta.updatedAt)));

        task.setProject(projectMapper.mapRow(rs, rowNum));
        task.setReporter(reporterMapper.mapRow(rs, rowNum));
        if (rs.getObject(assigneeMapper.generateColumnAlias(ProjectMemberMeta.id)) != null) {
            task.setAssignee(assigneeMapper.mapRow(rs, rowNum));
        }
        task.setType(typeMapper.mapRow(rs, rowNum));
        task.setPriority(priorityMapper.mapRow(rs, rowNum));
        task.setStatus(statusMapper.mapRow(rs, rowNum));

        return task;
    }
}
