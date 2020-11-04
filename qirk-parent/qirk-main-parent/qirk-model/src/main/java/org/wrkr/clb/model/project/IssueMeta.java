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
