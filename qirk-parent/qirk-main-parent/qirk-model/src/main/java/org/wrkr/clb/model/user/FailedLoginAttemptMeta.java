package org.wrkr.clb.model.user;

import org.wrkr.clb.model.BaseEntityMeta;

public class FailedLoginAttemptMeta extends BaseEntityMeta {

    public static final String TABLE_NAME = "failed_login_attempt";

    public static final String userId = "user_id";
    public static final String failedAt = "failed_at";
}
