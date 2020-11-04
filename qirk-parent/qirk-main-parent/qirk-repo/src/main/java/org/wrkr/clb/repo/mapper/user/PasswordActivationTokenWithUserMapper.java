package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.user.PasswordActivationToken;

public class PasswordActivationTokenWithUserMapper extends PasswordActivationTokenMapper {

    private AccountUserMapper userMapper;

    public PasswordActivationTokenWithUserMapper(String activationTokenTableName, String userTableName) {
        super(activationTokenTableName);
        userMapper = new AccountUserMapper(userTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                userMapper.generateSelectColumnsStatement();
    }

    @Override
    public PasswordActivationToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        PasswordActivationToken token = super.mapRow(rs, rowNum);
        token.setUser(userMapper.mapRow(rs, rowNum));
        return token;
    }
}
