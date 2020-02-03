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
package org.wrkr.clb.repo.project.imprt.jira;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraTaskMeta;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.repo.JDBCBaseMainRepo;


@Repository
public class ImportedJiraTaskRepo extends JDBCBaseMainRepo {

    private static final String INSERT = "INSERT INTO " + ImportedJiraTaskMeta.TABLE_NAME + " " +
            "(" + ImportedJiraTaskMeta.projectId + ", " + // 1
            ImportedJiraTaskMeta.taskId + ", " + // 2
            ImportedJiraTaskMeta.jiraTaskId + ") " + // 3
            "VALUES (?, ?, ?);";

    public void save(Task task, long jiraTaskId) {
        getJdbcTemplate().update(INSERT, task.getProjectId(), task.getId(), jiraTaskId);
    }
}
