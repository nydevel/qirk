package org.wrkr.clb.repo.mapper.project.task.attachment;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.task.attachment.Attachment;
import org.wrkr.clb.model.project.task.attachment.AttachmentMeta;

public class AttachmentFilenameMapper extends BaseMapper<Attachment> {

    public AttachmentFilenameMapper() {
        super();
    }

    public AttachmentFilenameMapper(String attachmentTableName) {
        super(attachmentTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(AttachmentMeta.id) + ", " +
                generateSelectColumnStatement(AttachmentMeta.filename);
    }

    @Override
    public Attachment mapRow(ResultSet rs, @SuppressWarnings("unused") int rowNum) throws SQLException {
        Attachment attachment = new Attachment();

        attachment.setId(rs.getLong(generateColumnAlias(AttachmentMeta.id)));
        attachment.setFilename(rs.getString(generateColumnAlias(AttachmentMeta.filename)));

        return attachment;
    }
}
