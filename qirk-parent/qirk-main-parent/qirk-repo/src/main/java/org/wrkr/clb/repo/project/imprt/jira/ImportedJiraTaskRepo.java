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
