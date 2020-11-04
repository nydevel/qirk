package org.wrkr.clb.repo.security;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.user.BaseUserMapper;

@Repository
public class SecurityUserRepo extends JDBCBaseMainRepo {

    private static final BaseUserMapper SECURITY_USER_MAPPER = new BaseUserMapper();

    private static final String SELECT_BY_ID_FOR_SECURITY = "SELECT " +
            SECURITY_USER_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + UserMeta.TABLE_NAME + " " +
            "WHERE " + UserMeta.id + " = ?;"; // 1

    public User getByIdForSecurity(Long userId) {
        return queryForObjectOrNull(SELECT_BY_ID_FOR_SECURITY, SECURITY_USER_MAPPER,
                userId);
    }
}
