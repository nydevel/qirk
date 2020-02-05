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

import java.sql.Array;
import java.sql.JDBCType;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskLinkMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;

@Repository
public class TaskLinkRepo extends JDBCBaseMainRepo {

    private static final String SELECT_1_BY_TASK1_ID_AND_TASK2_ID = "SELECT 1 FROM " + TaskLinkMeta.TABLE_NAME + " " +
            "WHERE " + TaskLinkMeta.task1Id + " = ? " + // 1
            "AND " + TaskLinkMeta.task2Id + " = ?;"; // 2

    private static final String INSERT = "INSERT INTO " + TaskLinkMeta.TABLE_NAME + " " +
            "(" + TaskLinkMeta.task1Id + ", " + // 1, 3
            TaskLinkMeta.task2Id + ") " + // 2, 4
            "VALUES (?, ?), (?, ?);";

    private static final String INSERT_BATCH = "INSERT INTO " + TaskLinkMeta.TABLE_NAME + " " +
            "(" + TaskLinkMeta.task1Id + ", " + // 1
            TaskLinkMeta.task2Id + ") " + // 2
            "VALUES (unnest(?), unnest(?));";

    private static final String DELETE = "DELETE FROM " + TaskLinkMeta.TABLE_NAME + " " +
            "WHERE " + TaskLinkMeta.task1Id + " = ? " + // 1
            "AND " + TaskLinkMeta.task2Id + " = ?;"; // 2

    public boolean exists(long task1Id, long task2Id) {
        return exists(SELECT_1_BY_TASK1_ID_AND_TASK2_ID, task1Id, task2Id);
    }

    public void save(long task1Id, long task2Id) {
        getJdbcTemplate().update(INSERT,
                task1Id, task2Id,
                task2Id, task1Id);
    }

    public void saveBatch(Task task, List<Task> linkedTasks) {
        Long[] task1Ids = new Long[2 * linkedTasks.size()];
        Long[] task2Ids = new Long[2 * linkedTasks.size()];
        for (int i = 0; i < linkedTasks.size(); i++) {
            task1Ids[i] = task.getId();
            task1Ids[linkedTasks.size() + i] = linkedTasks.get(i).getId();

            task2Ids[i] = linkedTasks.get(i).getId();
            task2Ids[linkedTasks.size() + i] = task.getId();
        }

        Array task1IdArray = createArrayOf(JDBCType.BIGINT.getName(), task1Ids);
        Array task2IdArray = createArrayOf(JDBCType.BIGINT.getName(), task2Ids);

        getJdbcTemplate().update(INSERT_BATCH,
                task1IdArray, task2IdArray);
    }

    public void delete(long task1Id, long task2Id) {
        getJdbcTemplate().batchUpdate(DELETE,
                Arrays.asList(new Object[] { task1Id, task2Id }, new Object[] { task2Id, task1Id }));
    }
}
