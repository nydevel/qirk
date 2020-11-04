package org.wrkr.clb.repo.project;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.InviteStatusMeta;
import org.wrkr.clb.model.project.InviteStatus.Status;
import org.wrkr.clb.repo.EnumEntityRepo;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.InviteStatusMapper;

@Repository
public class InviteStatusRepo extends JDBCBaseMainRepo implements EnumEntityRepo<InviteStatus, InviteStatus.Status> {

    private static final InviteStatusMapper STATUS_MAPPER = new InviteStatusMapper();

    private static final String SELECT_BY_ID = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + InviteStatusMeta.TABLE_NAME + " " +
            "WHERE " + InviteStatusMeta.id + " = ?;"; // 1

    private static final String SELECT_BY_NAME_CODE = "SELECT " +
            STATUS_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + InviteStatusMeta.TABLE_NAME + " " +
            "WHERE " + InviteStatusMeta.nameCode + " = ?;"; // 1

    public InviteStatus get(Long statusId) {
        return queryForObjectOrNull(SELECT_BY_ID, STATUS_MAPPER, statusId);
    }

    @Override
    public InviteStatus getByNameCode(Status status) {
        return queryForObjectOrNull(SELECT_BY_NAME_CODE, STATUS_MAPPER, status.toString());
    }
}
