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
package org.wrkr.clb.model.project;

import org.wrkr.clb.model.BaseIdEntityMeta;

public class IssueMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "issue";

    public static final String summary = "summary";
    public static final String description = "description";
    public static final String projectId = "project_id";
    public static final String reporterId = "user_id";
    public static final String sourceExternalId = "source_external_id";
    public static final String sourceURL = "source_url";
    public static final String createdAt = "created_at";
    public static final String taskId = "task_id";
}
