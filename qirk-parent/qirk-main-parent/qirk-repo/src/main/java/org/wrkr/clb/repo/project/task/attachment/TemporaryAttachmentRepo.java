package org.wrkr.clb.repo.project.task.attachment;

import java.sql.Array;
import java.sql.JDBCType;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.task.attachment.TemporaryAttachment;
import org.wrkr.clb.model.project.task.attachment.TemporaryAttachmentMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.task.attachment.TemporaryAttachmentPathMapper;

@Repository
public class TemporaryAttachmentRepo extends JDBCBaseMainRepo {

    private static final String INSERT = "INSERT INTO " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "(" + TemporaryAttachmentMeta.uuid + ", " + // 1
            TemporaryAttachmentMeta.filename + ", " + // 2
            TemporaryAttachmentMeta.path + ", " + // 3
            TemporaryAttachmentMeta.projectId + ", " + // 4
            TemporaryAttachmentMeta.createdAt + ") " + // 5
            "VALUES (?, ?, ?, ?, ?);";

    private static final String SELECT_1_BY_UUID = "SELECT 1 FROM " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "WHERE " + TemporaryAttachmentMeta.uuid + " = ?;"; // 1

    private static final TemporaryAttachmentPathMapper TEMPORARY_ATTACHMENT_MAPPER = new TemporaryAttachmentPathMapper(
            TemporaryAttachmentMeta.TABLE_NAME);

    private static final String SELECT_BY_CREATED_AT_UNTIL = "SELECT " +
            TEMPORARY_ATTACHMENT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "WHERE " + TemporaryAttachmentMeta.TABLE_NAME + "." + TemporaryAttachmentMeta.createdAt + " < ?;"; // 1

    private static final String DELETE_BY_PROJECT_ID_AND_UUIDS = "DELETE FROM " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "WHERE " + TemporaryAttachmentMeta.projectId + " = ? " + // 1
            "AND " + TemporaryAttachmentMeta.uuid + " = ANY(?);"; // 2

    private static final String DELETE_BY_CREATED_AT_UNTIL = "DELETE FROM " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "WHERE " + TemporaryAttachmentMeta.createdAt + " < ?;"; // 1

    public void save(TemporaryAttachment attachment) {
        getJdbcTemplate().update(INSERT,
                attachment.getUuid(), attachment.getFilename(), attachment.getPath(),
                attachment.getProjectId(), attachment.getCreatedAt());
    }

    public boolean existsByUuid(String uuid) {
        return exists(SELECT_1_BY_UUID, uuid);
    }

    public List<TemporaryAttachment> listByCreatedAtUntil(long timestamp) {
        return queryForList(SELECT_BY_CREATED_AT_UNTIL, TEMPORARY_ATTACHMENT_MAPPER, timestamp);
    }

    public void deleteByProjectIdAndUuids(Long projectId, List<String> uuids) {
        Array uuidArray = createArrayOf(JDBCType.VARCHAR.getName(), uuids.toArray());
        getJdbcTemplate().update(DELETE_BY_PROJECT_ID_AND_UUIDS,
                projectId, uuidArray);
    }

    public void deleteByCreatedAtUntil(long timestamp) {
        getJdbcTemplate().update(DELETE_BY_CREATED_AT_UNTIL,
                timestamp);
    }
}
