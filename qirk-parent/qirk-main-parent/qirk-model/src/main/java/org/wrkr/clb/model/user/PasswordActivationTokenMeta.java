package org.wrkr.clb.model.user;

import org.wrkr.clb.model.BaseIdEntityMeta;

public class PasswordActivationTokenMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "password_activation_token";

    public static final String token = "token";
    public static final String userId = "user_id";
    public static final String createdAt = "created_at";
}
