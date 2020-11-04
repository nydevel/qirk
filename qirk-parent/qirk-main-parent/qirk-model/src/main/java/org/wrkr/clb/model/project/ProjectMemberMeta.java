package org.wrkr.clb.model.project;

import org.wrkr.clb.model.BaseIdEntityMeta;

public class ProjectMemberMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "project_member";

    public static final String userId = "user_id";
    public static final String projectId = "project_id";
    public static final String writeAllowed = "write_allowed";
    public static final String manager = "manager";
    public static final String hiredAt = "hired_at";
    public static final String firedAt = "fired_at";
    public static final String fired = "fired";

    public static final ProjectMemberMeta DEFAULT = new ProjectMemberMeta(TABLE_NAME);

    public ProjectMemberMeta(String tableAlias) {
        super(tableAlias);
    }
}
