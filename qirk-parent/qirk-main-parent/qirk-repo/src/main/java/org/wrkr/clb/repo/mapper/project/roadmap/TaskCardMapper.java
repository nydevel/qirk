package org.wrkr.clb.repo.mapper.project.roadmap;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.roadmap.TaskCard;
import org.wrkr.clb.model.project.roadmap.TaskCardMeta;
import org.wrkr.clb.repo.mapper.VersionedEntityMapper;

public class TaskCardMapper extends VersionedEntityMapper<TaskCard> {

    public TaskCardMapper() {
        super();
    }

    public TaskCardMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(TaskCardMeta.name) + ", " +
                generateSelectColumnStatement(TaskCardMeta.status) + ", " +
                generateSelectColumnStatement(TaskCardMeta.active) + ", " +
                generateSelectColumnStatement(TaskCardMeta.createdAt) + ", " +
                generateSelectColumnStatement(TaskCardMeta.archievedAt);
    }

    @Override
    public TaskCard mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        TaskCard card = new TaskCard();

        card.setId(rs.getLong(generateColumnAlias(TaskCardMeta.id)));
        card.setRecordVersion(rs.getLong(generateColumnAlias(TaskCardMeta.recordVersion)));
        card.setName(rs.getString(generateColumnAlias(TaskCardMeta.name)));
        card.setStatus(TaskCard.Status.valueOf(rs.getString(generateColumnAlias(TaskCardMeta.status))));
        card.setActive(rs.getBoolean(generateColumnAlias(TaskCardMeta.active)));
        card.setCreatedAt(getOffsetDateTime(rs, generateColumnAlias(TaskCardMeta.createdAt)));
        card.setArchievedAt(getOffsetDateTime(rs, generateColumnAlias(TaskCardMeta.archievedAt)));

        return card;
    }
}
