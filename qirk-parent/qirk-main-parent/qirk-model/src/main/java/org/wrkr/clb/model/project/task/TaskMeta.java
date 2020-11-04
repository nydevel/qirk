package org.wrkr.clb.model.project.task;

import org.wrkr.clb.model.BaseVersionedEntityMeta;

public class TaskMeta extends BaseVersionedEntityMeta {

    public static final String TABLE_NAME = "task";

    public static final String projectId = "project_id";
    public static final String number = "number";
    public static final String descriptionMd = "description_md";
    public static final String descriptionHtml = "description_html";
    public static final String summary = "summary";
    public static final String reporterId = "reporter_project_member_id";
    public static final String assigneeId = "assignee_project_member_id";
    public static final String createdAt = "created_at";
    public static final String updatedAt = "updated_at";
    public static final String typeId = "type_id";
    public static final String priorityId = "priority_id";
    public static final String statusId = "status_id";
    public static final String hidden = "hidden";
    public static final String cardId = "task_card_id";

    public static final TaskHashtagToTaskMeta hashtags = new TaskHashtagToTaskMeta();
}
