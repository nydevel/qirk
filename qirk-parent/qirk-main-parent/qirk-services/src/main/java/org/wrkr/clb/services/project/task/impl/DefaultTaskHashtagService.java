package org.wrkr.clb.services.project.task.impl;

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

@Validated
@Service
public class DefaultTaskHashtagService implements TaskHashtagService {

    @Autowired
    private TaskHashtagRepo hashtagRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskHashtagDTO> searchByProjectId(User currentUser, String prefix, Long projectId) {
        // security start
        securityService.authzCanReadProject(currentUser, projectId);
        // security finish

        List<TaskHashtag> hashtagList = hashtagRepo.listByProjectIdAndNamePrefix(prefix, projectId);
        return TaskHashtagDTO.fromEntities(hashtagList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<TaskHashtagDTO> searchByProjectUiId(User currentUser, String prefix, String projectUiId) {
        // security start
        securityService.authzCanReadProject(currentUser, projectUiId);
        // security finish

        List<TaskHashtag> hashtagList = hashtagRepo.listByProjectUiIdAndNamePrefix(prefix, projectUiId);
        return TaskHashtagDTO.fromEntities(hashtagList);
    }
}
