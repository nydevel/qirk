package org.wrkr.clb.repo.user;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.user.UserFavorite;
import org.wrkr.clb.model.user.UserFavoriteMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.user.UserFavoriteWithProjectAndSecurityMembershipMapper;
import org.wrkr.clb.repo.security.SecurityProjectRepo;

@Repository
public class JDBCUserFavoriteRepo extends JDBCBaseMainRepo {

    private static final UserFavoriteWithProjectAndSecurityMembershipMapper USER_FAVORITE_WITH_SECURITY_MEMBERSHIP_MAPPER = new UserFavoriteWithProjectAndSecurityMembershipMapper(
            UserFavoriteMeta.TABLE_NAME, ProjectMeta.TABLE_NAME, ProjectMemberMeta.DEFAULT, UserMeta.DEFAULT);

    private static final String SELECT_BY_USER_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_FOR_SECURITY = "SELECT "
            + USER_FAVORITE_WITH_SECURITY_MEMBERSHIP_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserFavoriteMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + UserFavoriteMeta.TABLE_NAME + "." + UserFavoriteMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            SecurityProjectRepo.JOIN_NOT_FIRED_PROJECT_MEMBER_AND_USER_ON_PROJECT_ID_AND_USER_ID + " " +
            "WHERE " + UserFavoriteMeta.TABLE_NAME + "." + UserFavoriteMeta.userId + " = ?;"; // 2

    public List<UserFavorite> listByUserIdAndFetchProjectAndMembershipForSecurity(Long userId) {
        return queryForList(SELECT_BY_USER_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_FOR_SECURITY,
                USER_FAVORITE_WITH_SECURITY_MEMBERSHIP_MAPPER,
                userId, userId);
    }
}
