package org.wrkr.clb.repo.security;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.IssueMeta;
import org.wrkr.clb.model.project.ProjectMemberMeta;
import org.wrkr.clb.model.project.ProjectMeta;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.security.SecurityIssueWithProjectAndMembershipMapper;
import org.wrkr.clb.repo.mapper.security.SecurityIssueWithProjectMapper;

@Repository
public class SecurityIssueRepo extends JDBCBaseMainRepo {

    private static final SecurityIssueWithProjectMapper MAPPER = new SecurityIssueWithProjectMapper(
            IssueMeta.TABLE_NAME, ProjectMeta.DEFAULT);

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT_FOR_SECURITY = "SELECT " +
            MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + IssueMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + IssueMeta.TABLE_NAME + "." + IssueMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            "WHERE " + IssueMeta.TABLE_NAME + "." + IssueMeta.id + " = ?;"; // 1

    private static final SecurityIssueWithProjectAndMembershipMapper SECURITY_ISSUE_WITH_MEMBERSHIP_MAPPER = new SecurityIssueWithProjectAndMembershipMapper(
            IssueMeta.TABLE_NAME, ProjectMeta.DEFAULT, ProjectMemberMeta.DEFAULT, UserMeta.DEFAULT);

    private static final String SELECT_BY_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY = "SELECT "
            + SECURITY_ISSUE_WITH_MEMBERSHIP_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + IssueMeta.TABLE_NAME + " " +
            "INNER JOIN " + ProjectMeta.TABLE_NAME + " " +
            "ON " + IssueMeta.TABLE_NAME + "." + IssueMeta.projectId + " = " +
            ProjectMeta.TABLE_NAME + "." + ProjectMeta.id + " " +
            SecurityProjectRepo.JOIN_NOT_FIRED_PROJECT_MEMBER_AND_USER_ON_PROJECT_ID_AND_USER_ID + " " +
            "WHERE " + IssueMeta.TABLE_NAME + "." + IssueMeta.id + " = ?;"; // 2

    public Issue getByIdAndFetchProjectForSecurity(Long issueId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT_FOR_SECURITY, MAPPER,
                issueId);
    }

    public Issue getByIdAndFetchProjectAndMembershipByUserIdForSecurity(Long issueId, Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_AND_FETCH_PROJECT_AND_MEMBERSHIP_BY_USER_ID_FOR_SECURITY,
                SECURITY_ISSUE_WITH_MEMBERSHIP_MAPPER,
                userId, issueId);
    }
}
