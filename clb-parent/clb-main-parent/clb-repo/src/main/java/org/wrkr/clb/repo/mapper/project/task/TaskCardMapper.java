/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.task.TaskCard;
import org.wrkr.clb.model.project.task.TaskCardMeta;
import org.wrkr.clb.repo.mapper.RecordVersionMapper;

public class TaskCardMapper extends RecordVersionMapper<TaskCard> {

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
