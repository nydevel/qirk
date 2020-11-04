package org.wrkr.clb.services.project.imprt.jira.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.repo.project.imprt.jira.ImportedJiraTaskRepo;
import org.wrkr.clb.repo.project.task.TaskRepo;
import org.wrkr.clb.repo.project.task.ProjectTaskNumberSequenceRepo;
import org.wrkr.clb.services.project.imprt.jira.ImportedJiraTaskService;


@Service
public class DefaultImportedJiraTaskService implements ImportedJiraTaskService {

    @Autowired
    private ImportedJiraTaskRepo importedTaskRepo;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ProjectTaskNumberSequenceRepo sequenceRepo;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public void save(Task task, long jiraTaskId) {
        taskRepo.save(task);
        importedTaskRepo.save(task, jiraTaskId);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    public void saveAndUpdateTaskNumberSequence(Task task, long jiraTaskId) {
        ProjectTaskNumberSequence taskNumberSequence = sequenceRepo.getByProjectId(task.getProject().getId());
        task.setNumber(taskNumberSequence.getNextTaskNumber());
        taskNumberSequence.setNextTaskNumber(task.getNumber() + 1);
        sequenceRepo.update(taskNumberSequence);

        taskRepo.save(task);
        importedTaskRepo.save(task, jiraTaskId);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    public void update(Task task) {
        taskRepo.updateFromJira(task);
    }
}
