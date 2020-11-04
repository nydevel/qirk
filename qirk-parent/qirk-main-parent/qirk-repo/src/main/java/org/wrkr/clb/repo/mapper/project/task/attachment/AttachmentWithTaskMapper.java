package org.wrkr.clb.repo.mapper.project.task.attachment;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.model.project.task.attachment.Attachment;
import org.wrkr.clb.repo.mapper.project.task.TaskNumberMapper;

public class AttachmentWithTaskMapper extends AttachmentMapper {

    private TaskNumberMapper taskMapper;

    public AttachmentWithTaskMapper(String attachmentTableName, String taskTableName) {
        super(attachmentTableName);
        taskMapper = new TaskNumberMapper(taskTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return super.generateSelectColumnsStatement() + ", " +
                taskMapper.generateSelectColumnsStatement();
    }

    @Override
    public Attachment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Attachment attachment = super.mapRow(rs, rowNum);
        attachment.setTask(taskMapper.mapRow(rs, rowNum));
        return attachment;
    }
}
