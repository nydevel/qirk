package org.wrkr.clb.model.project.task.attachment;

import org.wrkr.clb.model.BaseIdEntityMeta;

public abstract class AttachmentMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "attachment";

    public static final String filename = "filename";
    public static final String externalPath = "external_path";
    public static final String taskId = "task_id";
    public static final String deleted = "deleted";
}
