package org.wrkr.clb.repo.project;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.ApplicationStatus;
import org.wrkr.clb.model.project.ApplicationStatusMeta;
import org.wrkr.clb.model.project.ApplicationStatus.Status;
import org.wrkr.clb.repo.EnumEntityRepo;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.ApplicationStatusMapper;

/**
 * @author Evgeny Poreykin
 *
 */
@Repository
public class ApplicationStatusRepo extends JDBCBaseMainRepo
        implements EnumEntityRepo<ApplicationStatus, ApplicationStatus.Status> {

    private static final ApplicationStatusMapper STATUS_MAPPER = new ApplicationStatusMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ApplicationStatusMeta.TABLE_NAME + " " +
            "WHERE " + ApplicationStatusMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NAME_CODE = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + ApplicationStatusMeta.TABLE_NAME + " " +
            "WHERE " + ApplicationStatusMeta.nameCode + " = ?;"; // 1

    public ApplicationStatus get(Long statusId) {
        return queryForObjectOrNull(SELECT_BY_ID, STATUS_MAPPER, statusId);
    }

    @Override
    public ApplicationStatus getByNameCode(Status status) {
        return queryForObjectOrNull(SELECT_BY_NAME_CODE, STATUS_MAPPER, status.toString());
    }
}
