package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.InviteStatusMeta;

public class InviteStatusMapper extends BaseMapper<InviteStatus> {

    public InviteStatusMapper() {
        super();
    }

    public InviteStatusMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(InviteStatusMeta.id) + ", " +
                generateSelectColumnStatement(InviteStatusMeta.nameCode);
    }

    @Override
    public InviteStatus mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        InviteStatus status = new InviteStatus();

        status.setId(rs.getLong(generateColumnAlias(InviteStatusMeta.id)));
        status.setNameCode(InviteStatus.Status.valueOf(rs.getString(generateColumnAlias(InviteStatusMeta.nameCode))));

        return status;
    }
}
