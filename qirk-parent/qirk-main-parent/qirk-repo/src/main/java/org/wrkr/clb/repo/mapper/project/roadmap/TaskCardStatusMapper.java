package org.wrkr.clb.repo.mapper.project.roadmap;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.roadmap.TaskCard;
import org.wrkr.clb.model.project.roadmap.TaskCardMeta;
import org.wrkr.clb.repo.mapper.VersionedEntityMapper;

public class TaskCardStatusMapper extends VersionedEntityMapper<TaskCard> {

    public TaskCardStatusMapper() {
        super();
    }

    public TaskCardStatusMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(TaskCardMeta.status);
    }

    @Override
    public TaskCard mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        TaskCard card = new TaskCard();

        card.setId(rs.getLong(generateColumnAlias(TaskCardMeta.id)));
        card.setStatus(TaskCard.Status.valueOf(rs.getString(generateColumnAlias(TaskCardMeta.status))));

        return card;
    }
}
