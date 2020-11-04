package org.wrkr.clb.model.user;

import org.wrkr.clb.model.BaseIdEntityMeta;

public class UserMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "user_profile";

    public static final String username = "username";
    public static final String emailAddress = "email_address";
    public static final String passwordHash = "password_hash";
    public static final String createdAt = "created_at";
    public static final String manager = "manager";
    public static final String fullName = "full_name";

    public static final UserMeta DEFAULT = new UserMeta(TABLE_NAME);

    public UserMeta(String tableAlias) {
        super(tableAlias);
    };
}
