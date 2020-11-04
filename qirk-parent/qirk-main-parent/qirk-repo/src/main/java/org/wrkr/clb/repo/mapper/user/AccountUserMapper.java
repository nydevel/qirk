package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserMeta;

public class AccountUserMapper extends UserMapper {

    public AccountUserMapper() {
        super();
    }

    public AccountUserMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(UserMeta.passwordHash);
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = super.mapRow(rs, rowNum);
        user.setPasswordHash(rs.getString(generateColumnAlias(UserMeta.passwordHash)));
        return user;
    }
}
