package org.wrkr.clb.repo.mapper.project.roadmap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import org.wrkr.clb.model.project.roadmap.Road;
import org.wrkr.clb.model.project.roadmap.RoadMeta;
import org.wrkr.clb.model.project.roadmap.TaskCard;
import org.wrkr.clb.model.project.roadmap.TaskCardMeta;
import org.wrkr.clb.model.project.task.Task;

public class RoadWithCardsAndTasksMapper extends RoadMapper {

    private TaskCardWithTaskMapper cardMapper;

    public RoadWithCardsAndTasksMapper(String roadTableName, String cardTableName, String taskTableName,
            String priorityTableName, String statusTableName) {
        super(roadTableName);
        cardMapper = new TaskCardWithTaskMapper(cardTableName, taskTableName, priorityTableName, statusTableName);
    }

    public String generateCardColumnAlias(String columnLabel) {
        return cardMapper.generateColumnAlias(columnLabel);
    }

    public String generateTaskColumnAlias(String columnLabel) {
        return cardMapper.generateTaskColumnAlias(columnLabel);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                cardMapper.generateSelectColumnsStatement();
    }

    @Override
    public Road mapRow(ResultSet rs, int rowNum) throws SQLException {
        Road road = super.mapRow(rs, rowNum);
        if (rs.getObject(generateCardColumnAlias(TaskCardMeta.id)) != null) {
            road.setCards(Arrays.asList(cardMapper.mapRow(rs, rowNum)));
        }
        return road;
    }

    public Road mapRow(Map<String, Object> result) {
        Road road = new Road();

        road.setId((Long) result.get(generateColumnAlias(RoadMeta.id)));
        road.setRecordVersion((Long) result.get(generateColumnAlias(RoadMeta.recordVersion)));
        road.setName((String) result.get(generateColumnAlias(RoadMeta.name)));
        road.setPreviousId((Long) result.get(generateColumnAlias(RoadMeta.previousId)));
        road.setDeleted((Boolean) result.get(generateColumnAlias(RoadMeta.deleted)));

        if (result.get(generateCardColumnAlias(TaskCardMeta.id)) != null) {
            road.getCards().add(mapRowForCard(result));
        }

        return road;
    }

    public TaskCard mapRowForCard(Map<String, Object> result) {
        return cardMapper.mapRow(result);
    }

    public Task mapRowForTask(Map<String, Object> result) {
        return cardMapper.mapRowForTask(result);
    }
}
