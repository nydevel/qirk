package org.wrkr.clb.repo.project.task;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.model.project.task.TaskSubscriberMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;

@Repository
public class TaskSubscriberRepo extends JDBCBaseMainRepo {

    private static final String EXISTS = "SELECT 1 FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.userId + " = ? " + // 1
            "AND " + TaskSubscriberMeta.taskId + " = ?;"; // 2

    private static final String INSERT = "INSERT INTO " + TaskSubscriberMeta.TABLE_NAME + " " +
            "(" + TaskSubscriberMeta.userId + ", " + // 1
            TaskSubscriberMeta.taskId + ") " + // 2
            "VALUES (?, ?);";

    private static final String SELECT_USER_IDS_BY_TASK_ID = "SELECT " +
            TaskSubscriberMeta.userId + " " +
            "FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.taskId + " = ?;"; // 1

    private static final String COUNT_USER_IDS_BY_TASK_ID = "SELECT COUNT(" +
            TaskSubscriberMeta.userId + ") " +
            "FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.taskId + " = ?;"; // 1

    private static final String DELETE = "DELETE FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.taskId + " = ? " + // 1
            "AND " + TaskSubscriberMeta.userId + " = ?;"; // 2

    private static final String DELETE_BY_USER_ID_AND_PROJECT_ID = "DELETE FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.userId + " = ? " + // 1
            "AND " + TaskSubscriberMeta.taskId + " IN ( " +
            "SELECT " + TaskMeta.id + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskMeta.projectId + " = ?);"; // 2

    private static final String DELETE_NOT_MEMBERS_BY_PROJECT_ID = "DELETE FROM " + TaskSubscriberMeta.TABLE_NAME + " " +
            "WHERE " + TaskSubscriberMeta.userId + " NOT IN (" +
            "SELECT " + ProjectMemberMeta.TABLE_NAME + "." + ProjectMemberMeta.userId + " " +
            "FROM " + ProjectMemberMeta.TABLE_NAME + " " +
            "WHERE " + ProjectMemberMeta.projectId + " = ? " + // 1
            "AND NOT " + ProjectMemberMeta.fired + ") " +
            "AND " + TaskSubscriberMeta.taskId + " IN ( " +
            "SELECT " + TaskMeta.id + " " +
            "FROM " + TaskMeta.TABLE_NAME + " " +
            "WHERE " + TaskMeta.projectId + " = ?);"; // 2

    public boolean exists(long userId, long taskId) {
        return exists(EXISTS, userId, taskId);
    }

    public void save(long userId, long taskId) {
        getJdbcTemplate().update(INSERT, userId, taskId);
    }

    public List<Long> listUserIdsByTaskId(long taskId) {
        return queryForList(SELECT_USER_IDS_BY_TASK_ID, Long.class, taskId);
    }

    public Long countByTaskId(Long taskId) {
        return queryForObjectOrNull(COUNT_USER_IDS_BY_TASK_ID, Long.class, taskId);
    }

    public void delete(long taskId, long userId) {
        getJdbcTemplate().update(DELETE, taskId, userId);
    }

    public void deleteByUserIdAndProjectId(long userId, Long projectId) {
        getJdbcTemplate().update(DELETE_BY_USER_ID_AND_PROJECT_ID, userId, projectId);
    }

    public void deleteNonMembersByProjectId(Long projectId) {
        getJdbcTemplate().update(DELETE_NOT_MEMBERS_BY_PROJECT_ID, projectId, projectId);
    }
}
