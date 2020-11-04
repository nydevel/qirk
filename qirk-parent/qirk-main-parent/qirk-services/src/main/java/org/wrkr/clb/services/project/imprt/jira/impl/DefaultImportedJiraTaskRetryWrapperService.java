package org.wrkr.clb.services.project.imprt.jira.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.common.jdbc.transaction.Executor;
import org.wrkr.clb.common.jdbc.transaction.RetryOnDuplicateKey;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.services.project.imprt.jira.ImportedJiraTaskRetryWrapperService;
import org.wrkr.clb.services.project.imprt.jira.ImportedJiraTaskService;


@Service
public class DefaultImportedJiraTaskRetryWrapperService implements ImportedJiraTaskRetryWrapperService {

    @Autowired
    private ImportedJiraTaskService taskService;

    @Override
    public void saveAndUpdateTaskNumberSequence(Task task, long jiraTaskId) throws Exception {
        RetryOnDuplicateKey.<Void>exec(new Executor() {
            @SuppressWarnings("unchecked")
            @Override
            public Void exec(@SuppressWarnings("unused") int retryNumber) throws Exception {
                taskService.saveAndUpdateTaskNumberSequence(task, jiraTaskId);
                return null;
            }
        });
    }
}
