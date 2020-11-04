package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserMeta;
import org.wrkr.clb.repo.mapper.BaseEntityMapper;

public class BaseUserMapper extends BaseEntityMapper<User> {

    public BaseUserMapper() {
        super();
    }

    public BaseUserMapper(String tableName) {
        super(tableName);
    }

    public BaseUserMapper(UserMeta userMeta) {
        super(userMeta);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(UserMeta.id) + ", " +
                generateSelectColumnStatement(UserMeta.manager);
    }

    @Override
    public User mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        User user = new User();

        user.setId(rs.getLong(generateColumnAlias(UserMeta.id)));
        user.setManager(rs.getBoolean(generateColumnAlias(UserMeta.manager)));

        return user;
    }

    public User mapRow(Map<String, Object> result) {
        User user = new User();

        user.setId((Long) result.get(generateColumnAlias(UserMeta.id)));
        user.setManager((boolean) result.get(generateColumnAlias(UserMeta.manager)));

        return user;
    }
}
