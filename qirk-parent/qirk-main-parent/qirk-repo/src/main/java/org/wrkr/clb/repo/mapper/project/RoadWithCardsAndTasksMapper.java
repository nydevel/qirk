/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
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
package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import org.wrkr.clb.model.project.Road;
import org.wrkr.clb.model.project.RoadMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskCard;
import org.wrkr.clb.model.project.task.TaskCardMeta;
import org.wrkr.clb.repo.mapper.project.task.TaskCardWithTaskMapper;

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
