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
import org.wrkr.clb.repo.JDBCIdEntityRepo;


@Repository
public class ProjectTaskNumberSequenceRepo extends JDBCIdEntityRepo {

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
