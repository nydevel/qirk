package org.wrkr.clb.model.project.task.attachment;

import org.wrkr.clb.model.BaseEntityMeta;

public abstract class TemporaryAttachmentMeta extends BaseEntityMeta {

    public static final String TABLE_NAME = "temporary_attachment";

    public static final String uuid = "uuid";
    public static final String filename = "filename";
    public static final String path = "path";
    public static final String projectId = "project_id";
    public static final String createdAt = "created_at";
}
