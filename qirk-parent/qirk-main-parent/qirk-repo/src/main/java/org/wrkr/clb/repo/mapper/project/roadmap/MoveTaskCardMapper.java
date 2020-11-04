package org.wrkr.clb.repo.mapper.project.roadmap;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.roadmap.TaskCard;
import org.wrkr.clb.model.project.roadmap.TaskCardMeta;
import org.wrkr.clb.repo.mapper.VersionedEntityMapper;

public class MoveTaskCardMapper extends VersionedEntityMapper<TaskCard> {

    protected String nextTableName = "";

    public MoveTaskCardMapper(String tableName, String nextTableName) {
        super(tableName);
        this.nextTableName = nextTableName;
    }

    public String generateNextColumnAlias(String columnLabel) {
        return nextTableName + "__" + columnLabel;
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(TaskCardMeta.projectId) + ", " +
                generateSelectColumnStatement(TaskCardMeta.roadId) + ", " +
                generateSelectColumnStatement(TaskCardMeta.previousId) + ", " +
                nextTableName + "." + TaskCardMeta.id + " AS " + generateNextColumnAlias(TaskCardMeta.id);
    }

    @Override
    public TaskCard mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        TaskCard card = new TaskCard();

        card.setId(rs.getLong(generateColumnAlias(TaskCardMeta.id)));
        card.setRecordVersion(rs.getLong(generateColumnAlias(TaskCardMeta.recordVersion)));
        card.setProjectId(rs.getLong(generateColumnAlias(TaskCardMeta.projectId)));
        card.setRoadId(rs.getLong(generateColumnAlias(TaskCardMeta.roadId)));
        card.setPreviousId((Long) rs.getObject(generateColumnAlias(TaskCardMeta.previousId))); // nullable
        card.setNextId((Long) rs.getObject(generateNextColumnAlias(TaskCardMeta.id))); // nullable

        return card;
    }
}
