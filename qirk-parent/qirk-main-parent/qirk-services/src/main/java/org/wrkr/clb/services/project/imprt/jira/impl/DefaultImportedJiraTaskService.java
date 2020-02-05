/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
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
