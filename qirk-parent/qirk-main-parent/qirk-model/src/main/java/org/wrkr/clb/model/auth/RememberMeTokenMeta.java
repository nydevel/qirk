package org.wrkr.clb.model.auth;

import org.wrkr.clb.model.BaseIdEntityMeta;

public class RememberMeTokenMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "remember_me_token";

    public static final String token = "token";
    public static final String userId = "user_id";
    public static final String createdAt = "created_at";
    public static final String updatedAt = "updated_at";
}
