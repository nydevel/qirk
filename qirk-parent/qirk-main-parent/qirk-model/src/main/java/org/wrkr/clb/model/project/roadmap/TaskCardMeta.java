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
package org.wrkr.clb.model.project.roadmap;

import org.wrkr.clb.model.BaseVersionedEntityMeta;

public class TaskCardMeta extends BaseVersionedEntityMeta {

    public static final String TABLE_NAME = "task_card";

    public static final String projectId = "project_id";
    public static final String roadId = "road_id";
    public static final String name = "name";
    public static final String status = "status";
    public static final String active = "active";
    public static final String previousId = "previous_id";
    public static final String createdAt = "created_at";
    public static final String archievedAt = "archieved_at";
}
