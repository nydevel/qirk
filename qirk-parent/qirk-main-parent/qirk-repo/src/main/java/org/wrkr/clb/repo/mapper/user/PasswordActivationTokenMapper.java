package org.wrkr.clb.repo.mapper.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.user.PasswordActivationToken;
import org.wrkr.clb.model.user.PasswordActivationTokenMeta;

public class PasswordActivationTokenMapper extends BaseMapper<PasswordActivationToken> {

    public PasswordActivationTokenMapper() {
        super();
    }

    public PasswordActivationTokenMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(PasswordActivationTokenMeta.id) + ", " +
                generateSelectColumnStatement(PasswordActivationTokenMeta.userId) + ", " +
                generateSelectColumnStatement(PasswordActivationTokenMeta.token) + ", " +
                generateSelectColumnStatement(PasswordActivationTokenMeta.createdAt);
    }

    @Override
    public PasswordActivationToken mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        PasswordActivationToken token = new PasswordActivationToken();

        token.setId(rs.getLong(generateColumnAlias(PasswordActivationTokenMeta.id)));
        token.setUserId(rs.getLong(generateColumnAlias(PasswordActivationTokenMeta.userId)));
        token.setToken(rs.getString(generateColumnAlias(PasswordActivationTokenMeta.token)));
        token.setCreatedAt(getOffsetDateTime(rs, generateColumnAlias(PasswordActivationTokenMeta.createdAt)));

        return token;
    }
}
