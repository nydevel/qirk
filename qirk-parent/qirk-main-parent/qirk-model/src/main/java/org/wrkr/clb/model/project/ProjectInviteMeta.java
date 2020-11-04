package org.wrkr.clb.model.project;

import org.wrkr.clb.model.BaseIdEntityMeta;

public class ProjectInviteMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "project_invite";

    public static final String senderId = "sender_user_id";
    public static final String userId = "user_id";
    public static final String projectId = "project_id";
    public static final String text = "text";
    public static final String createdAt = "created_at";
    public static final String updatedAt = "updated_at";
    public static final String statusId = "status_id";
    public static final String reported = "reported";
}
