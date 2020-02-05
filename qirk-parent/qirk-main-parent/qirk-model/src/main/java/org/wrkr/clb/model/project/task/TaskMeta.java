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
package org.wrkr.clb.model.project.task;

import org.wrkr.clb.model.BaseVersionedEntityMeta;

public class TaskMeta extends BaseVersionedEntityMeta {

    public static final String TABLE_NAME = "task";

    public static final String projectId = "project_id";
    public static final String number = "number";
    public static final String descriptionMd = "description_md";
    public static final String descriptionHtml = "description_html";
    public static final String summary = "summary";
    public static final String reporterId = "reporter_user_organization_id";
    public static final String assigneeId = "assignee_user_organization_id";
    public static final String createdAt = "created_at";
    public static final String updatedAt = "updated_at";
    public static final String typeId = "type_id";
    public static final String priorityId = "priority_id";
    public static final String statusId = "status_id";
    public static final String hidden = "hidden";
    public static final String cardId = "task_card_id";

    public static final TaskHashtagToTaskMeta hashtags = new TaskHashtagToTaskMeta();
}
