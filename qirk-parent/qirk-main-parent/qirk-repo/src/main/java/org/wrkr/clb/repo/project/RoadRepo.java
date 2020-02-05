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
package org.wrkr.clb.repo.project;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.BaseVersionedEntityMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.Road;
import org.wrkr.clb.model.project.RoadMeta;
import org.wrkr.clb.model.project.task.TaskCard;
import org.wrkr.clb.model.project.task.TaskCardMeta;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.model.project.task.TaskPriorityMeta;
import org.wrkr.clb.model.project.task.TaskStatusMeta;
import org.wrkr.clb.repo.JDBCBaseIdRepo;
import org.wrkr.clb.repo.mapper.RecordVersionMapper;
import org.wrkr.clb.repo.mapper.project.MoveRoadMapper;
import org.wrkr.clb.repo.mapper.project.RoadWithCardsAndTasksMapper;

@Repository
public class RoadRepo extends JDBCBaseIdRepo {

    private static final String INSERT = "INSERT INTO " +
            RoadMeta.TABLE_NAME + " " +
            "(" + RoadMeta.recordVersion + ", " + // 1
            RoadMeta.projectId + ", " + // 2
            RoadMeta.name + ", " + // 3
            RoadMeta.previousId + ") " + // 4
            "VALUES (?, ?, ?, ?) " +
            "RETURNING " + RoadMeta.id + ";";

    private static final String EXISTS_NOT_DELETED_BY_ID_AND_PROJECT_ID = "SELECT 1 FROM " + RoadMeta.TABLE_NAME + " " +
            "WHERE " + RoadMeta.id + " = ? " + // 1
            "AND " + RoadMeta.projectId + " = ? " + // 2
            "AND NOT " + RoadMeta.deleted + ";";

    private static final String SELECT_FIRST_NOT_DELETED_ID_BY_PROJECT_ID = "SELECT " + RoadMeta.id + " " +
            "FROM " + RoadMeta.TABLE_NAME + " " +
            "WHERE " + RoadMeta.projectId + " = ? " + // 1
            "AND " + RoadMeta.previousId + " IS NULL " +
            "AND NOT " + RoadMeta.deleted + ";";

    private static final String NEXT_ROAD_TABLE_ALIAS = "next_road";
    private static final String SELECT_LAST_NOT_DELETED_ID_BY_PROJECT_ID = "SELECT " +
            RoadMeta.TABLE_NAME + "." + RoadMeta.id + " " +
            "FROM " + RoadMeta.TABLE_NAME + " " +
            "LEFT JOIN " + RoadMeta.TABLE_NAME + " AS " + NEXT_ROAD_TABLE_ALIAS + " " +
            "ON " + RoadMeta.TABLE_NAME + "." + RoadMeta.id + " = " +
            NEXT_ROAD_TABLE_ALIAS + "." + RoadMeta.previousId + " " +
            "WHERE " + RoadMeta.TABLE_NAME + "." + RoadMeta.projectId + " = ? " + // 1
            "AND " + NEXT_ROAD_TABLE_ALIAS + "." + RoadMeta.projectId + " IS NULL " +
            "AND NOT " + RoadMeta.TABLE_NAME + "." + RoadMeta.deleted + ";";

    private static final RecordVersionMapper<Road> RECORD_VERSION_MAPPER = new RecordVersionMapper<Road>() {

        @Override
        public Road mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
            Road road = new Road();
            road.setId(rs.getLong(generateColumnAlias(BaseVersionedEntityMeta.id)));
            road.setRecordVersion(rs.getLong(generateColumnAlias(BaseVersionedEntityMeta.recordVersion)));
            return road;
        }
    };

    private static final String SELECT_NOT_DELETED_RECORD_VERSION_BY_ID = "SELECT " +
            RECORD_VERSION_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + RoadMeta.TABLE_NAME + " " +
            "WHERE " + RoadMeta.id + " = ? " + // 1
            "AND NOT " + RoadMeta.deleted + ";";

    private static final MoveRoadMapper MOVE_ROAD_MAPPER = new MoveRoadMapper(RoadMeta.TABLE_NAME, NEXT_ROAD_TABLE_ALIAS);

    private static final String SELECT_NOT_DELETED_FOR_MOVE_PREFIX = "SELECT " +
            MOVE_ROAD_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + RoadMeta.TABLE_NAME + " " +
            "LEFT JOIN " + RoadMeta.TABLE_NAME + " AS " + NEXT_ROAD_TABLE_ALIAS + " " +
            "ON " + RoadMeta.TABLE_NAME + "." + RoadMeta.id + " = " +
            NEXT_ROAD_TABLE_ALIAS + "." + RoadMeta.previousId + " " +
            "WHERE " + RoadMeta.TABLE_NAME + "." + RoadMeta.id + " = ? " + // 1
            "AND NOT " + RoadMeta.TABLE_NAME + "." + RoadMeta.deleted;

    private static final String SELECT_NOT_DELETED_FOR_MOVE_BY_ID = SELECT_NOT_DELETED_FOR_MOVE_PREFIX + ";";

    private static final String SELECT_NOT_DELETED_FOR_MOVE_BY_ID_AND_PROJECT_ID = SELECT_NOT_DELETED_FOR_MOVE_PREFIX + " " +
            "AND " + RoadMeta.TABLE_NAME + "." + RoadMeta.projectId + " = ?;"; // 2;

    private static final RoadWithCardsAndTasksMapper LIST_ROAD_MAPPER = new RoadWithCardsAndTasksMapper(
            RoadMeta.TABLE_NAME, TaskCardMeta.TABLE_NAME, TaskMeta.TABLE_NAME, TaskPriorityMeta.TABLE_NAME,
            TaskStatusMeta.TABLE_NAME);

    private static final String SELECT_BY_PROJECT_ID_AND_FETCH_CARDS_AND_TASKS = "SELECT " +
            LIST_ROAD_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + RoadMeta.TABLE_NAME + " " +
            "LEFT JOIN " + TaskCardMeta.TABLE_NAME + " " +
            "ON (" + RoadMeta.TABLE_NAME + "." + RoadMeta.id + " = " +
            TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.roadId + " " +
            "AND " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.active + ") " +
            "LEFT JOIN " + TaskMeta.TABLE_NAME + " " +
            "ON " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.id + " = " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.cardId + " " +
            "LEFT JOIN " + TaskPriorityMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.priorityId + " = " +
            TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.id + " " +
            "LEFT JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + RoadMeta.TABLE_NAME + "." + RoadMeta.projectId + " = ? " + // 1
            "AND NOT " + RoadMeta.TABLE_NAME + "." + RoadMeta.deleted + " " +
            "ORDER BY " + RoadMeta.TABLE_NAME + "." + RoadMeta.id + " ASC, " +
            TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.id + " ASC, " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.id + " DESC;";

    private static final String SELECT_BY_PROJECT_UI_ID_AND_FETCH_CARDS_AND_TASKS = "SELECT " +
            LIST_ROAD_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + RoadMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + RoadMeta.TABLE_NAME + "." + RoadMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "LEFT JOIN " + TaskCardMeta.TABLE_NAME + " " +
            "ON (" + RoadMeta.TABLE_NAME + "." + RoadMeta.id + " = " +
            TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.roadId + " " +
            "AND " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.active + ") " +
            "LEFT JOIN " + TaskMeta.TABLE_NAME + " " +
            "ON " + TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.id + " = " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.cardId + " " +
            "LEFT JOIN " + TaskPriorityMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.priorityId + " = " +
            TaskPriorityMeta.TABLE_NAME + "." + TaskPriorityMeta.id + " " +
            "LEFT JOIN " + TaskStatusMeta.TABLE_NAME + " " +
            "ON " + TaskMeta.TABLE_NAME + "." + TaskMeta.statusId + " = " +
            TaskStatusMeta.TABLE_NAME + "." + TaskStatusMeta.id + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.uiId + " = ? " + // 1
            "AND NOT " + RoadMeta.TABLE_NAME + "." + RoadMeta.deleted + " " +
            "ORDER BY " + RoadMeta.TABLE_NAME + "." + RoadMeta.id + " ASC, " +
            TaskCardMeta.TABLE_NAME + "." + TaskCardMeta.id + " ASC, " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.id + " DESC;";

    private static final String UPDATE_RECORD_VERSION_AND_NAME = "UPDATE " + RoadMeta.TABLE_NAME + " " +
            "SET " + RoadMeta.recordVersion + " = ?, " + // 1
            RoadMeta.name + " = ? " + // 2
            "WHERE " + RoadMeta.id + " = ?;"; // 3

    private static final String UPDATE_PREVIOUS_ID = "UPDATE " + RoadMeta.TABLE_NAME + " " +
            "SET " + RoadMeta.previousId + " = ? " + // 1
            "WHERE " + RoadMeta.id + " = ?;"; // 2

    private static final String UPDATE_PREVIOUS_ID_SET_NULL = "UPDATE " + RoadMeta.TABLE_NAME + " " +
            "SET " + RoadMeta.previousId + " = NULL " +
            "WHERE " + RoadMeta.id + " = ?;"; // 1

    private static final String UPDATE_DELETED_SET_TRUE = "UPDATE " + RoadMeta.TABLE_NAME + " " +
            "SET " + RoadMeta.deleted + " = true " +
            "WHERE " + RoadMeta.id + " = ?;"; // 1

    private static final String DELETE = "DELETE FROM " + RoadMeta.TABLE_NAME + " " +
            "WHERE " + RoadMeta.id + " = ?;"; // 1

    public Road save(Road road) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT, new String[] { RoadMeta.id });
            ps.setLong(1, road.getRecordVersion());
            ps.setLong(2, road.getProjectId());
            ps.setString(3, road.getName());
            ps.setObject(4, road.getPreviousId(), Types.BIGINT); // nullable
            return ps;
        }, keyHolder);

        return setIdAfterSave(road, keyHolder);
    }

    public boolean existsByIdAndProjectId(Long roadId, Long projectId) {
        return exists(EXISTS_NOT_DELETED_BY_ID_AND_PROJECT_ID, roadId, projectId);
    }

    public Long getFirstIdByProjectId(Long projectId) {
        return queryForObjectOrNull(SELECT_FIRST_NOT_DELETED_ID_BY_PROJECT_ID, Long.class, projectId);
    }

    public Long getLastIdByProjectId(Long projectId) {
        return queryForObjectOrNull(SELECT_LAST_NOT_DELETED_ID_BY_PROJECT_ID, Long.class, projectId);
    }

    public Road getRecordVersionById(Long roadId) {
        return queryForObjectOrNull(SELECT_NOT_DELETED_RECORD_VERSION_BY_ID, RECORD_VERSION_MAPPER, roadId);
    }

    public Road getForMoveById(Long roadId) {
        return queryForObjectOrNull(SELECT_NOT_DELETED_FOR_MOVE_BY_ID, MOVE_ROAD_MAPPER, roadId);
    }

    public Road getForMoveByIdAndProjectId(Long roadId, Long projectId) {
        return queryForObjectOrNull(SELECT_NOT_DELETED_FOR_MOVE_BY_ID_AND_PROJECT_ID, MOVE_ROAD_MAPPER, roadId, projectId);
    }

    private List<Road> listAndFetchCardsAndTasks(List<Map<String, Object>> rows) {
        List<Road> results = new ArrayList<Road>();

        for (Map<String, Object> row : rows) {
            Road lastRoad = (results.isEmpty() ? null : results.get(results.size() - 1));

            if (lastRoad == null || !lastRoad.getId().equals(
                    (Long) row.get(LIST_ROAD_MAPPER.generateColumnAlias(RoadMeta.id)))) {
                results.add(LIST_ROAD_MAPPER.mapRow(row));

            } else {
                TaskCard lastCard = lastRoad.getCards().get(lastRoad.getCards().size() - 1);
                if (!lastCard.getId().equals(
                        (Long) row.get(LIST_ROAD_MAPPER.generateCardColumnAlias(TaskCardMeta.id)))) {
                    lastRoad.getCards().add(LIST_ROAD_MAPPER.mapRowForCard(row));
                }

                else {
                    lastCard.getTasks().add(LIST_ROAD_MAPPER.mapRowForTask(row));
                }
            }
        }

        return results;
    }

    public List<Road> listByProjectIdAndFetchCardsAndTasks(Long projectId) {
        return listAndFetchCardsAndTasks(
                getJdbcTemplate().queryForList(SELECT_BY_PROJECT_ID_AND_FETCH_CARDS_AND_TASKS, projectId));
    }

    public List<Road> listByProjectUiIdAndFetchCardsAndTasks(String projectUiId) {
        return listAndFetchCardsAndTasks(
                getJdbcTemplate().queryForList(SELECT_BY_PROJECT_UI_ID_AND_FETCH_CARDS_AND_TASKS, projectUiId));
    }

    public void updateRecordVersionAndName(Road road) {
        updateSingleRow(UPDATE_RECORD_VERSION_AND_NAME, road.getRecordVersion(), road.getName(), road.getId());
    }

    public void updatePreviousId(Long previousId, Long roadId) {
        updateSingleRow(UPDATE_PREVIOUS_ID, previousId, roadId);
    }

    public void setPreviousIdToNull(Long roadId) {
        updateSingleRow(UPDATE_PREVIOUS_ID_SET_NULL, roadId);
    }

    public void setDeletedToTrue(Long roadId) {
        updateSingleRow(UPDATE_DELETED_SET_TRUE, roadId);
    }

    public void delete(Long roadId) {
        updateSingleRow(DELETE, roadId);
    }
}
