package org.wrkr.clb.repo.mapper.project;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.ApplicationStatus;
import org.wrkr.clb.model.project.ApplicationStatusMeta;

public class ApplicationStatusMapper extends BaseMapper<ApplicationStatus> {

    public ApplicationStatusMapper() {
        super();
    }

    public ApplicationStatusMapper(String tableName) {
        super(tableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(ApplicationStatusMeta.id) + ", " +
                generateSelectColumnStatement(ApplicationStatusMeta.nameCode);
    }

    @Override
    public ApplicationStatus mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        ApplicationStatus status = new ApplicationStatus();

        status.setId(rs.getLong(generateColumnAlias(ApplicationStatusMeta.id)));
        status.setNameCode(ApplicationStatus.Status.valueOf(rs.getString(generateColumnAlias(ApplicationStatusMeta.nameCode))));

        return status;
    }
}
