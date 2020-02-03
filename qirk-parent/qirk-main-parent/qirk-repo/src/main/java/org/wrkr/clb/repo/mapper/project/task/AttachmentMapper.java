/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.repo.mapper.project.task;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.wrkr.clb.common.jdbc.BaseMapper;
import org.wrkr.clb.model.project.task.Attachment;
import org.wrkr.clb.model.project.task.AttachmentMeta;
import org.wrkr.clb.repo.mapper.DropboxSettingsMapper;

public class AttachmentMapper extends BaseMapper<Attachment> {

    private DropboxSettingsMapper dropboxSettingsMapper;

    public AttachmentMapper(String attachmentTableName, String dropboxSettingsTableName) {
        super(attachmentTableName);
        dropboxSettingsMapper = new DropboxSettingsMapper(dropboxSettingsTableName);
    }

    @Override
    public String generateSelectColumnsStatement() {
        return generateSelectColumnStatement(AttachmentMeta.id) + ", " +
                generateSelectColumnStatement(AttachmentMeta.filename) + ", " +
                generateSelectColumnStatement(AttachmentMeta.path) + ", " +
                generateSelectColumnStatement(AttachmentMeta.taskId) + ", " +
                generateSelectColumnStatement(AttachmentMeta.dropboxSettingsId) + ", " +
                dropboxSettingsMapper.generateSelectColumnsStatement();
    }

    @Override
    public Attachment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Attachment attachment = new Attachment();

        attachment.setId(rs.getLong(generateColumnAlias(AttachmentMeta.id)));
        attachment.setFilename(rs.getString(generateColumnAlias(AttachmentMeta.filename)));
        attachment.setPath(rs.getString(generateColumnAlias(AttachmentMeta.path)));
        attachment.setTaskId(rs.getLong(generateColumnAlias(AttachmentMeta.taskId)));

        attachment.setDropboxSettingsId((Long) rs.getObject(generateColumnAlias(AttachmentMeta.dropboxSettingsId))); // nullable
        if (attachment.getDropboxSettingsId() != null) {
            attachment.setDropboxSettings(dropboxSettingsMapper.mapRow(rs, rowNum));
        }

        return attachment;
    }
}
