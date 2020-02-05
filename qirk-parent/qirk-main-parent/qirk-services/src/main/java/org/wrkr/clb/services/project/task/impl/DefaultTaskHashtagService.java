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
package org.wrkr.clb.services.project.task.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.task.TaskHashtagRepo;
import org.wrkr.clb.services.dto.project.task.TaskHashtagDTO;
import org.wrkr.clb.services.project.task.TaskHashtagService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;

@Validated
@Service
public class DefaultTaskHashtagService implements TaskHashtagService {

    @Autowired
    private TaskHashtagRepo hashtagRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskHashtagDTO> searchByProjectId(User currentUser, String prefix, Long projectId, boolean includeUsed) {
        // security start
        securityService.authzCanReadProject(currentUser, projectId);
        // security finish

        List<TaskHashtag> hashtagList = new ArrayList<TaskHashtag>();
        if (includeUsed) {
            hashtagList = hashtagRepo.listByProjectIdAndNamePrefixAndFetchTasksCount(prefix, projectId);
        } else {
            hashtagList = hashtagRepo.listByProjectIdAndNamePrefix(prefix, projectId);
        }
        return TaskHashtagDTO.fromEntities(hashtagList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskHashtagDTO> searchByProjectUiId(User currentUser, String prefix, String projectUiId, boolean includeUsed) {
        // security start
        securityService.authzCanReadProject(currentUser, projectUiId);
        // security finish

        List<TaskHashtag> hashtagList = new ArrayList<TaskHashtag>();
        if (includeUsed) {
            hashtagList = hashtagRepo.listByProjectUiIdAndNamePrefixAndFetchTasksCount(prefix, projectUiId);
        } else {
            hashtagList = hashtagRepo.listByProjectUiIdAndNamePrefix(prefix, projectUiId);
        }
        return TaskHashtagDTO.fromEntities(hashtagList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void delete(User currentUser, Long id) throws ApplicationException {
        // security start
        Long hashtagId = securityService.authzCanDeleteTaskHashtag(currentUser, id);
        // security finish

        if (hashtagId == null) {
            throw new NotFoundException("Task hashtag");
        }
        hashtagRepo.deleteById(hashtagId);
    }
}
