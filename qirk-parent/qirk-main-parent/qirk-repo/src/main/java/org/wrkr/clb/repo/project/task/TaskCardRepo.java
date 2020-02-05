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
package org.wrkr.clb.repo.project.task;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.RoadMeta;
import org.wrkr.clb.model.project.task.TaskCard;
import org.wrkr.clb.model.project.task.TaskCardMeta;
import org.wrkr.clb.repo.JDBCBaseIdRepo;
import org.wrkr.clb.repo.mapper.project.task.MoveTaskCardMapper;
import org.wrkr.clb.repo.mapper.project.task.TaskCardMapper;
import org.wrkr.clb.repo.mapper.project.task.TaskCardStatusMapper;
import org.wrkr.clb.repo.mapper.project.task.TaskCardWithRoadMapper;

@Repository
public class TaskCardRepo extends JDBCBaseIdRepo {

    private static final String INSERT = "INSERT INTO " +
            TaskCardMeta.TABLE_NAME + " " +
            "(" + RoadMeta.recordVersion + ", " + // 1
            TaskCardMeta.projectId + ", " + // 2
            TaskCardMeta.roadId + ", " + // 3
            TaskCardMeta.name + ", " + // 4
            TaskCardMeta.status + ", " + // 5
            TaskCardMeta.active + ", " + // 6
            TaskCardMeta.createdAt + ") " + // 7
            "VALUES (?, ?, ?, ?, ?, ?, ?) " +
            "RETURNING " + TaskCardMeta.id + ";";

    private static final String EXISTS_BY_ROAD_ID = "SELECT 1 FROM " + TaskCardMeta.TABLE_NAME + " " +
            "WHERE " + TaskCardMeta.roadId + " = ?;"; // 1

    private static final String SELECT_FIRST_ACTIVE_ID_BY_ROAD_ID = "SELECT " + TaskCardMeta.id + " " +
            "FROM " + TaskCardMeta.TABLE_NAME + " " +
            "WHERE " + TaskCardMeta.roadId + " = ? " + // 1
            "AND " + TaskCardMeta.previousId + " IS NULL " +
            "AND " + TaskCardMeta.active + ";";

    private static final TaskCardStatusMapper TASK_CARD_STATUS_MAPPER = new TaskCardStatusMapper();

    private static final String SELECT_ACTIVE_BY_ID_AND_PROJECT_ID = "SELECT " +
            TASK_CARD_STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskCardMeta.TABLE_NAME + " " +
            "WHERE " + TaskCardMeta.id + " = ? " + // 1
            "AND " + TaskCardMeta.projectId + " = ? " + // 2
            "AND " + TaskCardMeta.active + ";";

    private static final TaskCardMapper TASK_CARD_MAPPER = new TaskCardMapper();

    private static final String SELECT_ACTIVE_BY_ID = "SELECT " +
            TASK_CARD_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskCardMeta.TABLE_NAME + " " +
            "WHERE " + TaskCardMeta.id + " = ? " + // 1
            "AND " + TaskCardMeta.active + ";";

    private static final String NEXT_TASK_CARD_TABLE_ALIAS = "next_card";
    private static final MoveTaskCardMapper MOVE_TASK_CARD_MAPPER = new MoveTaskCardMapper(TaskCardMeta.TABLE_NAME,
            NEXT_TASK_CARD_TABLE_ALIAS);

    private static final String SELECT_FOR_MOVE_PREFIX = "SELECT " +
            MOVE_TASK_CARD_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskCardMeta.TABLE_NAME + " " +
            "LEFT JOIN " + TaskCardMeta.TABLE_NAME + " AS " + NEXT_TASK_CARD_TABLE_ALIAS + " " +
            "ON " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.id + " = " +
            NEXT_TASK_CARD_TABLE_ALIAS + "." + TaskCardMeta.previousId + " " +
            "WHERE " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.id + " = ? "; // 1

    private static final String SELECT_FOR_MOVE_BY_ID = SELECT_FOR_MOVE_PREFIX + ";";

    private static final String SELECT_ACTIVE_FOR_MOVE_PREFIX = SELECT_FOR_MOVE_PREFIX + " " +
            "AND " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.active;

    private static final String SELECT_ACTIVE_FOR_MOVE_BY_ID = SELECT_ACTIVE_FOR_MOVE_PREFIX + ";";

    private static final String SELECT_FOR_MOVE_BY_ID_AND_ROAD_ID = SELECT_ACTIVE_FOR_MOVE_PREFIX + " " +
            "AND " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.roadId + " = ?;"; // 2

    private static final String SELECT_IDS_BY_PROJECT_ID_AND_STATUS = "SELECT " + TaskCardMeta.id + " " +
            "FROM " + TaskCardMeta.TABLE_NAME + " " +
            "WHERE " + TaskCardMeta.projectId + " = ? " + // 1
            "AND " + TaskCardMeta.status + " = ?;"; // 2

    private static final TaskCardWithRoadMapper TASK_CARD_WITH_ROAD_MAPPER = new TaskCardWithRoadMapper(
            TaskCardMeta.TABLE_NAME, RoadMeta.TABLE_NAME);

    private static final String SELECT_BY_PROJECT_ID_AND_FETCH_ROAD_PREFIX = "SELECT " +
            TASK_CARD_WITH_ROAD_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskCardMeta.TABLE_NAME + " " +
            "INNER JOIN " + RoadMeta.TABLE_NAME + " " +
            "ON " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.roadId + " = " +
            RoadMeta.TABLE_NAME + "." + RoadMeta.id + " " +
            "WHERE " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.projectId + " = ?"; // 1

    private static final String SELECT_ACTIVE_BY_PROJECT_ID_AND_FETCH_ROAD = "" +
            SELECT_BY_PROJECT_ID_AND_FETCH_ROAD_PREFIX + " " +
            "AND " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.active + ";";

    private static final String SELECT_NOT_ACTIVE_BY_PROJECT_ID_AND_FETCH_ROAD_AND_ORDER_BY_ARCHIEVED_AT_DESC = "" +
            SELECT_BY_PROJECT_ID_AND_FETCH_ROAD_PREFIX + " " +
            "AND NOT " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.active + " " +
            "ORDER BY " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.archievedAt + " DESC;";

    private static final String SELECT_BY_PROJECT_UI_ID_AND_FETCH_ROAD_PREFIX = "SELECT " +
            TASK_CARD_WITH_ROAD_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TaskCardMeta.TABLE_NAME + " " +
            "INNER JOIN " + RoadMeta.TABLE_NAME + " " +
            "ON " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.roadId + " = " +
            RoadMeta.TABLE_NAME + "." + RoadMeta.id + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + RoadMeta.TABLE_NAME + "." + RoadMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ?"; // 1

    private static final String SELECT_ACTIVE_BY_PROJECT_UI_ID_AND_FETCH_ROAD = "" +
            SELECT_BY_PROJECT_UI_ID_AND_FETCH_ROAD_PREFIX + " " +
            "AND " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.active + ";";

    private static final String SELECT_NOT_ACTIVE_BY_PROJECT_UI_ID_AND_FETCH_ROAD_AND_ORDER_BY_ARCHIEVED_AT_DESC = "" +
            SELECT_BY_PROJECT_UI_ID_AND_FETCH_ROAD_PREFIX + " " +
            "AND NOT " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.active + " " +
            "ORDER BY " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.archievedAt + " DESC;";

    private static final String UPDATE_STATUSAND_INC_RECORD_VERSION = "UPDATE " + TaskCardMeta.TABLE_NAME + " " +
            "SET " + TaskCardMeta.status + " = ?, " + // 1
            TaskCardMeta.recordVersion + " = " + TaskCardMeta.recordVersion + " + 1 " +
            "WHERE " + TaskCardMeta.id + " = ?;"; // 2

    private static final String UPDATE_RECORD_VERSION_AND_NAME_AND_STATUS = "UPDATE " + TaskCardMeta.TABLE_NAME + " " +
            "SET " + TaskCardMeta.recordVersion + " = ?, " + // 1
            TaskCardMeta.name + " = ?, " + // 2
            TaskCardMeta.status + " = ? " + // 3
            "WHERE " + TaskCardMeta.id + " = ?;"; // 4

    private static final String UPDATE_PREVIOUS_ID = "UPDATE " + TaskCardMeta.TABLE_NAME + " " +
            "SET " + TaskCardMeta.previousId + " = ? " + // 1
            "WHERE " + TaskCardMeta.id + " = ?;"; // 2

    private static final String UPDATE_ROAD_ID_AND_PREVIOUS_ID = "UPDATE " + TaskCardMeta.TABLE_NAME + " " +
            "SET " + TaskCardMeta.roadId + " = ?, " + // 1
            TaskCardMeta.previousId + " = ? " + // 2
            "WHERE " + TaskCardMeta.id + " = ?;"; // 3

    private static final String UPDATE_RECORD_VERSION_AND_ACTIVE_AND_ARCHIEVED_AT = "UPDATE " + TaskCardMeta.TABLE_NAME + " " +
            "SET " + TaskCardMeta.recordVersion + " = ?, " + // 1
            TaskCardMeta.active + " = ?, " + // 2
            TaskCardMeta.archievedAt + " = ? " + // 3
            "WHERE " + TaskCardMeta.id + " = ?;"; // 4

    private static final String UPDATE_PREV_ID_NULL_ACTIVE_FALSE_AND_ARCHIEVED_AT_NOW_AND_INC_VERSION_FOR_ACTIVE_BY_ROAD_ID = "" +
            "UPDATE " + TaskCardMeta.TABLE_NAME + " " +
            "SET " + TaskCardMeta.previousId + " = NULL, " +
            TaskCardMeta.recordVersion + " = " + TaskCardMeta.recordVersion + " + 1, " +
            TaskCardMeta.active + " = false, " +
            TaskCardMeta.archievedAt + " = NOW() " +
            "WHERE " + TaskCardMeta.roadId + " = ? " + // 1
            "AND " + TaskCardMeta.active + ";";

    private static final String DELETE = "DELETE FROM " + TaskCardMeta.TABLE_NAME + " " +
            "WHERE " + TaskCardMeta.id + " = ?;"; // 1

    public TaskCard save(TaskCard card) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT, new String[] { TaskCardMeta.id });
            ps.setLong(1, card.getRecordVersion());
            ps.setLong(2, card.getProjectId());
            ps.setLong(3, card.getRoadId());
            ps.setString(4, card.getName());
            ps.setString(5, card.getStatus().toString());
            ps.setBoolean(6, card.isActive());
            ps.setTimestamp(7, Timestamp.from(card.getCreatedAt().toInstant()));
            return ps;
        }, keyHolder);

        return setIdAfterSave(card, keyHolder);
    }

    public boolean existsByRoadId(Long roadId) {
        return exists(EXISTS_BY_ROAD_ID, roadId);
    }

    public Long getFirstIdByRoadId(Long roadId) {
        return queryForObjectOrNull(SELECT_FIRST_ACTIVE_ID_BY_ROAD_ID, Long.class, roadId);
    }

    public TaskCard getActiveByIdAndProjectId(Long cardId, Long projectId) {
        TaskCard card = queryForObjectOrNull(SELECT_ACTIVE_BY_ID_AND_PROJECT_ID, TASK_CARD_STATUS_MAPPER,
                cardId, projectId);
        if (card != null) {
            card.setActive(true);
        }
        return card;
    }

    public TaskCard getActiveById(Long cardId) {
        return queryForObjectOrNull(SELECT_ACTIVE_BY_ID, TASK_CARD_MAPPER, cardId);
    }

    public TaskCard getForMoveById(Long cardId) {
        return queryForObjectOrNull(SELECT_FOR_MOVE_BY_ID, MOVE_TASK_CARD_MAPPER, cardId);
    }

    public TaskCard getActiveForMoveById(Long cardId) {
        return queryForObjectOrNull(SELECT_ACTIVE_FOR_MOVE_BY_ID, MOVE_TASK_CARD_MAPPER, cardId);
    }

    public TaskCard getActiveForMoveByIdAndRoadId(Long cardId, Long roadId) {
        return queryForObjectOrNull(SELECT_FOR_MOVE_BY_ID_AND_ROAD_ID, MOVE_TASK_CARD_MAPPER, cardId, roadId);
    }

    public List<Long> listIdsByProjectIdAndStatus(Long projectId, TaskCard.Status status) {
        return queryForList(SELECT_IDS_BY_PROJECT_ID_AND_STATUS, Long.class,
                projectId, status.toString());
    }

    public List<TaskCard> listActiveByProjectIdAndFetchRoad(Long projectId) {
        return queryForList(SELECT_ACTIVE_BY_PROJECT_ID_AND_FETCH_ROAD,
                TASK_CARD_WITH_ROAD_MAPPER,
                projectId);
    }

    public List<TaskCard> listActiveByProjectUiIdAndFetchRoad(String projectUiId) {
        return queryForList(SELECT_ACTIVE_BY_PROJECT_UI_ID_AND_FETCH_ROAD,
                TASK_CARD_WITH_ROAD_MAPPER,
                projectUiId);
    }

    public List<TaskCard> listNotActiveByProjectIdAndFetchRoadAndOrderByArchievedAtDesc(Long projectId) {
        return queryForList(SELECT_NOT_ACTIVE_BY_PROJECT_ID_AND_FETCH_ROAD_AND_ORDER_BY_ARCHIEVED_AT_DESC,
                TASK_CARD_WITH_ROAD_MAPPER,
                projectId);
    }

    public List<TaskCard> listNotActiveByProjectUiIdAndFetchRoadAndOrderByArchievedAtDesc(String projectUiId) {
        return queryForList(SELECT_NOT_ACTIVE_BY_PROJECT_UI_ID_AND_FETCH_ROAD_AND_ORDER_BY_ARCHIEVED_AT_DESC,
                TASK_CARD_WITH_ROAD_MAPPER,
                projectUiId);
    }

    public Long updateStatusAndIncRecordVersion(TaskCard.Status status, Long cardId) {
        updateSingleRow(UPDATE_STATUSAND_INC_RECORD_VERSION, status.toString(), cardId);
        return null;
    }

    public void updateRecordVersionAndNameAndStatus(TaskCard card) {
        updateSingleRow(UPDATE_RECORD_VERSION_AND_NAME_AND_STATUS,
                card.getRecordVersion(), card.getName(), card.getStatus().toString(), card.getId());
    }

    public void updatePreviousId(Long previousId, Long cardId) {
        updateSingleRow(UPDATE_PREVIOUS_ID, previousId, cardId);
    }

    public void updateRoadIdAndPreviousIdById(Long roadId, Long previousId, Long cardId) {
        updateSingleRow(UPDATE_ROAD_ID_AND_PREVIOUS_ID, roadId, previousId, cardId);
    }

    public void updateRecordVersionAndActiveAndArchievedAt(TaskCard card) {
        updateSingleRow(UPDATE_RECORD_VERSION_AND_ACTIVE_AND_ARCHIEVED_AT,
                card.getRecordVersion(), card.isActive(), Timestamp.from(card.getArchievedAt().toInstant()), card.getId());
    }

    public void setPrevIdToNullActiveToFalseAndArchievedAtToNowAndIncVersionForActiveByRoadId(Long roadId) {
        getJdbcTemplate().update(UPDATE_PREV_ID_NULL_ACTIVE_FALSE_AND_ARCHIEVED_AT_NOW_AND_INC_VERSION_FOR_ACTIVE_BY_ROAD_ID,
                roadId);
    }

    public void delete(Long cardId) {
        updateSingleRow(DELETE, cardId);
    }
}
