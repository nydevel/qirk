package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserMeta;

public class UserMapper extends PublicUserMapper {

    public UserMapper() {
        super();
    }

    public UserMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(UserMeta.emailAddress);
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = super.mapRow(rs, rowNum);
        user.setEmailAddress(rs.getString(generateColumnAlias(UserMeta.emailAddress)));
        return user;
    }
}
