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
package org.wrkr.clb.model.organization;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.project.task.Attachment;


@Entity
@Table(name = DropboxSettingsMeta.TABLE_NAME)
public class DropboxSettings extends BaseIdEntity {

    @Column(name = "purge_on_delete", nullable = false)
    private boolean purgeOnDelete = false;

    @Column(name = "token", nullable = false)
    private String token;

    @OneToMany(mappedBy = "dropboxSettings", fetch = FetchType.LAZY)
    private List<Attachment> attachments = new ArrayList<Attachment>();

    public boolean isPurgeOnDelete() {
        return purgeOnDelete;
    }

    public void setPurgeOnDelete(boolean purgeOnDelete) {
        this.purgeOnDelete = purgeOnDelete;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

}
