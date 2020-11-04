package org.wrkr.clb.repo.project;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.GrantedPermissionsProjectInviteMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;

@Repository
public class JDBCGrantedPermissionsProjectInviteRepo extends JDBCBaseMainRepo {

    private static final String SELECT_PROJECT_IDS_BY_USER_ID_AND_STATUS_ID = "SELECT " +
            GrantedPermissionsProjectInviteMeta.projectId + " " +
            "FROM " + GrantedPermissionsProjectInviteMeta.TABLE_NAME + " " +
            "WHERE " + GrantedPermissionsProjectInviteMeta.userId + " = ? " + // 1
            "AND " + GrantedPermissionsProjectInviteMeta.statusId + " = ?;"; // 2

    public List<Long> listProjectIdsByUserIdAndStatusId(Long userId, Long statusId) {
        return queryForList(SELECT_PROJECT_IDS_BY_USER_ID_AND_STATUS_ID, Long.class, userId, statusId);
    }
}
