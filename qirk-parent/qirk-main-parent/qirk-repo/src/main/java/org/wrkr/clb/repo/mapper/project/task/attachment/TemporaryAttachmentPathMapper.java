package org.wrkr.clb.repo.mapper.project.task.attachment;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.task.attachment.TemporaryAttachment;
import org.wrkr.clb.model.project.task.attachment.TemporaryAttachmentMeta;

public class TemporaryAttachmentPathMapper extends BaseMapper<TemporaryAttachment> {

    public TemporaryAttachmentPathMapper(String attachmentTableName) {
        super(attachmentTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(TemporaryAttachmentMeta.path);
    }

    @Override
    public TemporaryAttachment mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        TemporaryAttachment attachment = new TemporaryAttachment();
        attachment.setPath(rs.getString(generateColumnAlias(TemporaryAttachmentMeta.path)));
        return attachment;
    }
}
