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
package org.wrkr.clb.model.project.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.organization.DropboxSettings;


@Entity
@Table(name = AttachmentMeta.TABLE_NAME)
public class Attachment extends BaseIdEntity {

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "path", nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
    @Transient
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropbox_settings_id")
    private DropboxSettings dropboxSettings;
    @Transient
    private Long dropboxSettingsId;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public DropboxSettings getDropboxSettings() {
        return dropboxSettings;
    }

    public void setDropboxSettings(DropboxSettings dropboxSettings) {
        this.dropboxSettings = dropboxSettings;
    }

    public Long getTaskId() {
        if (taskId == null) {
            return (task == null ? null : task.getId());
        }
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getDropboxSettingsId() {
        if (dropboxSettingsId == null) {
            return (dropboxSettings == null ? null : dropboxSettings.getId());
        }
        return dropboxSettingsId;
    }

    public void setDropboxSettingsId(Long dropboxSettingsId) {
        this.dropboxSettingsId = dropboxSettingsId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

}
