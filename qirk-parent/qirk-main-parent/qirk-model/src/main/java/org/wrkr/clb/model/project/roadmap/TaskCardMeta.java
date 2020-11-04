package org.wrkr.clb.model.project.roadmap;

import org.wrkr.clb.model.BaseVersionedEntityMeta;

public class TaskCardMeta extends BaseVersionedEntityMeta {

    public static final String TABLE_NAME = "task_card";

    public static final String projectId = "project_id";
    public static final String roadId = "road_id";
    public static final String name = "name";
    public static final String status = "status";
    public static final String active = "active";
    public static final String previousId = "previous_id";
    public static final String createdAt = "created_at";
    public static final String archievedAt = "archieved_at";
}
