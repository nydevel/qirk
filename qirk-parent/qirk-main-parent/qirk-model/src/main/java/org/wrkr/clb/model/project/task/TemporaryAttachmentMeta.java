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

import org.wrkr.clb.model.BaseEntityMeta;

public abstract class TemporaryAttachmentMeta extends BaseEntityMeta {

    public static final String TABLE_NAME = "temporary_attachment";

    public static final String uuid = "uuid";
    public static final String filename = "filename";
    public static final String path = "path";
    public static final String projectId = "project_id";
    public static final String dropboxSettingsId = "dropbox_settings_id";
    public static final String createdAt = "created_at";
}
