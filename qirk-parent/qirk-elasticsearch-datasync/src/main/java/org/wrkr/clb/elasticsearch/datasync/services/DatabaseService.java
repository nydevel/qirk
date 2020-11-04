package org.wrkr.clb.elasticsearch.datasync.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.JDBCGrantedPermissionsProjectInviteRepo;
import org.wrkr.clb.repo.project.JDBCProjectInviteRepo;
import org.wrkr.clb.repo.project.task.TaskRepo;
import org.wrkr.clb.repo.user.JDBCUserRepo;

public class DatabaseService {

    @Autowired
    private JDBCUserRepo userRepo;

    @Autowired
    private JDBCGrantedPermissionsProjectInviteRepo grantedPermissionsProjectInviteRepo;

    @Autowired
    private JDBCProjectInviteRepo projectInviteRepo;

    @Autowired
    private TaskRepo taskRepo;

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<User> getAllUsers() {
        return userRepo.listAndFetchProjectMembership();
    }

    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<Long> getInvitedProjectIdsByUser(User user, InviteStatus status) {
        List<Long> invitedProjectIds = grantedPermissionsProjectInviteRepo.listProjectIdsByUserIdAndStatusId(user.getId(),
                status.getId());
        invitedProjectIds.addAll(projectInviteRepo.listProjectIdsByUserIdAndStatusId(user.getId(), status.getId()));
        return invitedProjectIds;
    }

    @Transactional(value = "jpaTransactionManager", readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepo.listAndFetchTypeAndPriorityAndStatusAndHashtags();
    }
}
