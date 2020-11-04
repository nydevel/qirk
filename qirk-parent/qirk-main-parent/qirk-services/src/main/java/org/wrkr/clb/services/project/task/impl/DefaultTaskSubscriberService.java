package org.wrkr.clb.services.project.task.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.task.TaskSubscriberRepo;
import org.wrkr.clb.repo.user.JDBCUserRepo;
import org.wrkr.clb.services.dto.user.UserIdsDTO;
import org.wrkr.clb.services.project.task.TaskSubscriberService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.security.SecurityService;

@Service
public class DefaultTaskSubscriberService implements TaskSubscriberService {

    @Autowired
    private TaskSubscriberRepo subscriberRepo;

    @Autowired
    private JDBCUserRepo userRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private SecurityService authnSecurityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public void create(Long userId, Long taskId) {
        subscriberRepo.save(userId, taskId);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void create(User currentUser, Long taskId) {
        // security
        securityService.authzCanSubscribeToTask(currentUser, taskId);
        // security
        Long userId = currentUser.getId();
        if (!subscriberRepo.exists(userId, taskId)) {
            create(userId, taskId);
        }
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<Long> list(Long taskId) {
        return subscriberRepo.listUserIdsByTaskId(taskId);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public UserIdsDTO list(User currentUser, Long taskId) {
        // security
        securityService.authzCanReadTask(currentUser, taskId);
        // security
        return new UserIdsDTO(list(taskId));
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<User> listWithEmail(Long taskId, NotificationSettings.Setting notifSetting) {
        return userRepo.listBySubscribedTaskIdAndFetchNotificationSetting(taskId, notifSetting);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public void delete(Long userId, Long taskId) {
        subscriberRepo.delete(taskId, userId);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void delete(User currentUser, Long taskId) {
        // security
        authnSecurityService.isAuthenticated(currentUser);
        // security
        delete(currentUser.getId(), taskId);
    }
}
