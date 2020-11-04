package org.wrkr.clb.services.project.imprt.jira;

import org.wrkr.clb.model.project.task.Task;

public interface ImportedJiraTaskService {

    public void save(Task task, long jiraTaskId);

    public void saveAndUpdateTaskNumberSequence(Task task, long jiraTaskId);

    public void update(Task task);
}
