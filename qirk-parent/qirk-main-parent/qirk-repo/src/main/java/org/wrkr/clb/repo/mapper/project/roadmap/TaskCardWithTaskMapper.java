package org.wrkr.clb.repo.mapper.project.roadmap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import org.wrkr.clb.model.project.roadmap.TaskCard;
import org.wrkr.clb.model.project.roadmap.TaskCardMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.repo.mapper.project.task.ShortTaskWithPriorityAndStatusMapper;

public class TaskCardWithTaskMapper extends TaskCardMapper {

    private ShortTaskWithPriorityAndStatusMapper taskMapper;

    public TaskCardWithTaskMapper(String cardTableName, String taskTableName,
            String priorityTableName, String statusTableName) {
        super(cardTableName);
        taskMapper = new ShortTaskWithPriorityAndStatusMapper(taskTableName, priorityTableName, statusTableName);
    }

    public String generateTaskColumnAlias(String columnLabel) {
        return taskMapper.generateColumnAlias(columnLabel);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(TaskCardMeta.previousId) + ", " +
                taskMapper.generateSelectColumnsStatement();
    }

    @Override
    public TaskCard mapRow(ResultSet rs, int rowNum) throws SQLException {
        TaskCard card = super.mapRow(rs, rowNum);

        card.setPreviousId((Long) rs.getObject(generateColumnAlias(TaskCardMeta.previousId))); // nullable
        if (rs.getObject(generateTaskColumnAlias(TaskMeta.id)) != null) {
            card.setTasks(Arrays.asList(taskMapper.mapRow(rs, rowNum)));
        }

        return card;
    }

    public TaskCard mapRow(Map<String, Object> result) {
        TaskCard card = new TaskCard();

        card.setId((Long) result.get(generateColumnAlias(TaskCardMeta.id)));
        card.setRecordVersion((Long) result.get(generateColumnAlias(TaskCardMeta.recordVersion)));
        card.setName((String) result.get(generateColumnAlias(TaskCardMeta.name)));
        card.setStatus(TaskCard.Status.valueOf((String) result.get(generateColumnAlias(TaskCardMeta.status))));
        card.setActive((Boolean) result.get(generateColumnAlias(TaskCardMeta.active)));
        card.setCreatedAt(getOffsetDateTime(result, generateColumnAlias(TaskCardMeta.createdAt)));
        card.setArchievedAt(getOffsetDateTime(result, generateColumnAlias(TaskCardMeta.archievedAt)));
        card.setPreviousId((Long) result.get(generateColumnAlias(TaskCardMeta.previousId)));

        if (result.get(generateTaskColumnAlias(TaskMeta.id)) != null) {
            card.getTasks().add(mapRowForTask(result));
        }

        return card;
    }

    public Task mapRowForTask(Map<String, Object> result) {
        return taskMapper.mapRow(result);
    }
}
