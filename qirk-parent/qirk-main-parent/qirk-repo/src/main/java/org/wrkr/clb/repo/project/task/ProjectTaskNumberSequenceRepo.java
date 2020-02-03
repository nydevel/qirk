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
package org.wrkr.clb.repo.project.task;

import java.sql.PreparedStatement;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequenceMeta;
import org.wrkr.clb.repo.JDBCBaseIdRepo;


@Repository
public class ProjectTaskNumberSequenceRepo extends JDBCBaseIdRepo {

    private static final String INSERT = "INSERT INTO " + ProjectTaskNumberSequenceMeta.TABLE_NAME + " " +
            "(" + ProjectTaskNumberSequenceMeta.nextTaskNumber + ") " + // 1
            "VALUES (?) " +
            "RETURNING " + ProjectTaskNumberSequenceMeta.id + ";";

    private static final String SELECT_NEXT_TASK_NUMBER_BY_ID = "SELECT " +
            ProjectTaskNumberSequenceMeta.TABLE_NAME + "." + ProjectTaskNumberSequenceMeta.nextTaskNumber + " " +
            "FROM " + ProjectTaskNumberSequenceMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + ProjectTaskNumberSequenceMeta.TABLE_NAME + "." + ProjectTaskNumberSequenceMeta.id + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.taskNumberSequenceId + " " +
            "WHERE " + ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " = ?;"; // 1

    private static final String UPDATE = "UPDATE " + ProjectTaskNumberSequenceMeta.TABLE_NAME + " " +
            "SET " + ProjectTaskNumberSequenceMeta.nextTaskNumber + " = ? " + // 1
            "WHERE " + ProjectTaskNumberSequenceMeta.id + " = ?;"; // 2

    public ProjectTaskNumberSequence save(ProjectTaskNumberSequence sequence) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT,
                    new String[] { ProjectTaskNumberSequenceMeta.id });
            ps.setLong(1, sequence.getNextTaskNumber());
            return ps;
        }, keyHolder);

        return setIdAfterSave(sequence, keyHolder);
    }

    public ProjectTaskNumberSequence getByProjectId(Long sequenceId) {
        Long nextTaskNumber = queryForObjectOrNull(SELECT_NEXT_TASK_NUMBER_BY_ID, Long.class, sequenceId);

        ProjectTaskNumberSequence sequence = new ProjectTaskNumberSequence();
        sequence.setId(sequenceId);
        sequence.setNextTaskNumber(nextTaskNumber);
        return sequence;
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.REQUIRED)
    public void update(ProjectTaskNumberSequence sequence) {
        updateSingleRow(UPDATE, sequence.getNextTaskNumber(), sequence.getId());
    }
}
