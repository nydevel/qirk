package org.wrkr.clb.repo.project;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ProjectInviteMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;

@Repository
public class JDBCProjectInviteRepo extends JDBCBaseMainRepo {

    private static final String SELECT_PROJECT_IDS_BY_USER_ID_AND_STATUS_ID = "SELECT " +
            ProjectInviteMeta.projectId + " " +
            "FROM " + ProjectInviteMeta.TABLE_NAME + " " +
            "WHERE " + ProjectInviteMeta.userId + " = ? " + // 1
            "AND " + ProjectInviteMeta.statusId + " = ?;"; // 2

    public List<Long> listProjectIdsByUserIdAndStatusId(Long userId, Long statusId) {
        return queryForList(SELECT_PROJECT_IDS_BY_USER_ID_AND_STATUS_ID, Long.class, userId, statusId);
    }
}
