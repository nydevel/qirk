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

import org.wrkr.clb.model.BaseUiIdEntityMeta;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.repo.mapper.UiIdMapper;

public class TaskNumberWithProjectUiIdMapper extends TaskNumberMapper {

    private UiIdMapper<Project> projectMapper;

    public TaskNumberWithProjectUiIdMapper(String taskTableName, String projectTableName) {
        super(taskTableName);
        projectMapper = new UiIdMapper<Project>(projectTableName) {

            @Override
            public Project mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
                Project project = new Project();
                project.setId(rs.getLong(generateColumnAlias(BaseUiIdEntityMeta.id)));
                project.setUiId(rs.getString(generateColumnAlias(BaseUiIdEntityMeta.uiId)));
                return project;
            }
        };
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                projectMapper.generateSelectColumnsStatement();
    }

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
        Task task = super.mapRow(rs, rowNum);
        task.setProject(projectMapper.mapRow(rs, rowNum));
        return task;
    }
}
