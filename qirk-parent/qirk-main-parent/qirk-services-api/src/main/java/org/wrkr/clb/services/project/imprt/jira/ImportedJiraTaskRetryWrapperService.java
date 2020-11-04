package org.wrkr.clb.services.project.imprt.jira;

import org.wrkr.clb.model.project.task.Task;

public interface ImportedJiraTaskRetryWrapperService {

    public void saveAndUpdateTaskNumberSequence(Task task, long jiraTaskId) throws Exception;
}
