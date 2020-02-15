/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
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
package org.wrkr.clb.repo.project.task.attachment;

import java.sql.Array;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.project.task.TaskMeta;
import org.wrkr.clb.model.project.task.attachment.Attachment;
import org.wrkr.clb.model.project.task.attachment.AttachmentMeta;
import org.wrkr.clb.model.project.task.attachment.TemporaryAttachmentMeta;
import org.wrkr.clb.repo.JDBCIdEntityRepo;
import org.wrkr.clb.repo.mapper.project.task.attachment.AttachmentFilenameMapper;
import org.wrkr.clb.repo.mapper.project.task.attachment.AttachmentMapper;
import org.wrkr.clb.repo.mapper.project.task.attachment.AttachmentWithTaskMapper;

@Repository
public class AttachmentRepo extends JDBCIdEntityRepo {

    private static final String INSERT = "INSERT INTO " + AttachmentMeta.TABLE_NAME + " " +
            "(" + AttachmentMeta.filename + ", " + // 1
            AttachmentMeta.externalPath + ", " + // 2
            AttachmentMeta.taskId + ") " + // 3
            "VALUES (?, ?, ?) " +
            "RETURNING " + AttachmentMeta.id + ";";

    private static final String INSERT_BATCH = "INSERT INTO " + AttachmentMeta.TABLE_NAME + " " +
            "(" + AttachmentMeta.filename + ", " +
            AttachmentMeta.externalPath + ", " +
            AttachmentMeta.taskId + ") " +
            "(SELECT " + TemporaryAttachmentMeta.TABLE_NAME + "." + TemporaryAttachmentMeta.filename + ", " +
            TemporaryAttachmentMeta.TABLE_NAME + "." + TemporaryAttachmentMeta.path + ", " +
            "? " + // 1
            "FROM " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "WHERE " + TemporaryAttachmentMeta.TABLE_NAME + "." + TemporaryAttachmentMeta.projectId + " = ? " + // 2
            "AND " + TemporaryAttachmentMeta.TABLE_NAME + "." + TemporaryAttachmentMeta.uuid + " = ANY(?)) " + // 3
            "RETURNING " + AttachmentMeta.id + ", " +
            AttachmentMeta.filename + ";";

    private static final AttachmentMapper ATTACHMENT_MAPPER = new AttachmentMapper(AttachmentMeta.TABLE_NAME);

    private static final String SELECT_NOT_DELETED_BY_ID_AND_FETCH_DROPBOX_SETTINGS = "SELECT " +
            ATTACHMENT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + AttachmentMeta.TABLE_NAME + " " +
            "WHERE " + AttachmentMeta.TABLE_NAME + "." + AttachmentMeta.id + " = ? " + // 1
            "AND NOT " + AttachmentMeta.TABLE_NAME + "." + AttachmentMeta.deleted + ";";

    private static final AttachmentWithTaskMapper ATTACHMENT_WITH_TASK_MAPPER = new AttachmentWithTaskMapper(
            AttachmentMeta.TABLE_NAME, TaskMeta.TABLE_NAME);

    private static final String SELECT_NOT_DELETED_BY_ID_AND_FETCH_DROPBOX_SETTINGS_AND_TASK = "SELECT " +
            ATTACHMENT_WITH_TASK_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + AttachmentMeta.TABLE_NAME + " " +
            "INNER JOIN " + TaskMeta.TABLE_NAME + " " +
            "ON " + AttachmentMeta.TABLE_NAME + "." + AttachmentMeta.taskId + " = " +
            TaskMeta.TABLE_NAME + "." + TaskMeta.id + " " +
            "WHERE " + AttachmentMeta.TABLE_NAME + "." + AttachmentMeta.id + " = ? " + // 1
            "AND NOT " + AttachmentMeta.TABLE_NAME + "." + AttachmentMeta.deleted + ";";

    private static final AttachmentFilenameMapper ATTACHMENT_FILENAME_MAPPER = new AttachmentFilenameMapper();

    private static final String SELECT_NOT_DELETED_BY_TASK_ID = "SELECT " +
            ATTACHMENT_FILENAME_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + AttachmentMeta.TABLE_NAME + " " +
            "WHERE " + AttachmentMeta.TABLE_NAME + "." + AttachmentMeta.taskId + " = ? " + // 1
            "AND NOT " + AttachmentMeta.TABLE_NAME + "." + AttachmentMeta.deleted + ";";

    private static final String UPDATE_DELETED_SET_TRUE = "UPDATE " + AttachmentMeta.TABLE_NAME + " " +
            "SET " + AttachmentMeta.deleted + " = true " +
            "WHERE " + AttachmentMeta.id + " = ?;"; // 1

    public Attachment save(Attachment attachment) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT,
                    new String[] { AttachmentMeta.id });
            ps.setString(1, attachment.getFilename());
            ps.setString(2, attachment.getPath());
            ps.setLong(3, attachment.getTaskId());
            return ps;
        }, keyHolder);

        return setIdAfterSave(attachment, keyHolder);
    }

    public List<Attachment> saveBatch(Long taskId, Long projectId, List<String> uuids) {
        Array uuidArray = createArrayOf(JDBCType.VARCHAR.getName(), uuids.toArray());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        getJdbcTemplate().update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_BATCH,
                    new String[] { AttachmentMeta.id, AttachmentMeta.filename });
            ps.setLong(1, taskId);
            ps.setLong(2, projectId);
            ps.setArray(3, uuidArray);
            return ps;
        }, keyHolder);

        List<Attachment> attachments = new ArrayList<Attachment>();
        for (Map<String, Object> map : keyHolder.getKeyList()) {
            Attachment attachment = new Attachment();
            attachment.setId((Long) map.get(AttachmentMeta.id));
            attachment.setFilename((String) map.get(AttachmentMeta.filename));
            attachments.add(attachment);
        }
        return attachments;
    }

    public Attachment getNotDeletedByIdAndFetchDropboxSettings(Long attachmentId) {
        return queryForObjectOrNull(SELECT_NOT_DELETED_BY_ID_AND_FETCH_DROPBOX_SETTINGS, ATTACHMENT_MAPPER, attachmentId);
    }

    public Attachment getNotDeletedByIdAndFetchDropboxSettingsAndTask(Long attachmentId) {
        return queryForObjectOrNull(SELECT_NOT_DELETED_BY_ID_AND_FETCH_DROPBOX_SETTINGS_AND_TASK, ATTACHMENT_WITH_TASK_MAPPER,
                attachmentId);
    }

    public List<Attachment> listNotDeletedByTaskId(Long taskId) {
        return queryForList(SELECT_NOT_DELETED_BY_TASK_ID, ATTACHMENT_FILENAME_MAPPER, taskId);
    }

    public void setDeletedToTrue(Long attachmentId) {
        updateSingleRow(UPDATE_DELETED_SET_TRUE, attachmentId);
    }
}
