package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserMeta;

public class PublicUserMapper extends BaseUserMapper {

    public PublicUserMapper() {
        super();
    }

    public PublicUserMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                generateSelectColumnStatement(UserMeta.username) + ", " +
                generateSelectColumnStatement(UserMeta.fullName);
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = super.mapRow(rs, rowNum);

        user.setUsername(rs.getString(generateColumnAlias(UserMeta.username)));
        user.setFullName(rs.getString(generateColumnAlias(UserMeta.fullName)));

        return user;
    }

    @Override
    public User mapRow(Map<String, Object> result) {
        User user = super.mapRow(result);

        user.setUsername((String) result.get(generateColumnAlias(UserMeta.username)));
        user.setFullName((String) result.get(generateColumnAlias(UserMeta.fullName)));

        return user;
    }
}
