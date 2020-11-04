package org.wrkr.clb.model.project;

import org.wrkr.clb.model.BaseIdEntityMeta;

public class ProjectInviteTokenMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "project_invite_token";

    public static final String inviteId = "invite_id";
    public static final String token = "token";
    public static final String emailAddress = "email_address";
    public static final String createdAt = "created_at";
}
