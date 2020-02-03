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
package org.wrkr.clb.repo.project.task;

import java.sql.Array;
import java.sql.JDBCType;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.wrkr.clb.model.organization.DropboxSettingsMeta;
import org.wrkr.clb.model.project.task.TemporaryAttachment;
import org.wrkr.clb.model.project.task.TemporaryAttachmentMeta;
import org.wrkr.clb.repo.JDBCBaseMainRepo;
import org.wrkr.clb.repo.mapper.project.task.TemporaryAttachmentPathMapper;


@Repository
public class TemporaryAttachmentRepo extends JDBCBaseMainRepo {

    private static final String INSERT = "INSERT INTO " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "(" + TemporaryAttachmentMeta.uuid + ", " + // 1
            TemporaryAttachmentMeta.filename + ", " + // 2
            TemporaryAttachmentMeta.path + ", " + // 3
            TemporaryAttachmentMeta.projectId + ", " + // 4
            TemporaryAttachmentMeta.dropboxSettingsId + ", " + // 5
            TemporaryAttachmentMeta.createdAt + ") " + // 6
            "VALUES (?, ?, ?, ?, ?, ?);";

    private static final String SELECT_1_BY_UUID = "SELECT 1 FROM " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "WHERE " + TemporaryAttachmentMeta.uuid + " = ?;"; // 1

    private static final TemporaryAttachmentPathMapper TEMPORARY_ATTACHMENT_MAPPER = new TemporaryAttachmentPathMapper(
            TemporaryAttachmentMeta.TABLE_NAME, DropboxSettingsMeta.TABLE_NAME);

    private static final String SELECT_BY_CREATED_AT_UNTIL = "SELECT " +
            TEMPORARY_ATTACHMENT_MAPPER.generateSelectColumnsStatement() + " " +
            "FROM " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "INNER JOIN " + DropboxSettingsMeta.TABLE_NAME + " " +
            "ON " + TemporaryAttachmentMeta.TABLE_NAME + "." + TemporaryAttachmentMeta.dropboxSettingsId + " = " +
            DropboxSettingsMeta.TABLE_NAME + "." + DropboxSettingsMeta.id + " " +
            "WHERE " + TemporaryAttachmentMeta.TABLE_NAME + "." + TemporaryAttachmentMeta.createdAt + " < ?;"; // 1

    private static final String DELETE_BY_PROJECT_ID_AND_UUIDS = "DELETE FROM " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "WHERE " + TemporaryAttachmentMeta.projectId + " = ? " + // 1
            "AND " + TemporaryAttachmentMeta.uuid + " = ANY(?);"; // 2

    private static final String DELETE_BY_CREATED_AT_UNTIL = "DELETE FROM " + TemporaryAttachmentMeta.TABLE_NAME + " " +
            "WHERE " + TemporaryAttachmentMeta.createdAt + " < ?;"; // 1

    public void save(TemporaryAttachment attachment) {
        getJdbcTemplate().update(INSERT,
                attachment.getUuid(), attachment.getFilename(), attachment.getPath(),
                attachment.getProjectId(), attachment.getDropboxSettingsId(), attachment.getCreatedAt());
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
