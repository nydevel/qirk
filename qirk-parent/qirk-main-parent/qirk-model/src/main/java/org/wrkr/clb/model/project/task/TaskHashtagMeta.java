package org.wrkr.clb.model.project.task;

import org.wrkr.clb.model.BaseIdEntityMeta;

public class TaskHashtagMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "task_hashtag";

    public static final String projectId = "project_id";
    public static final String name = "name";

    public static final TaskHashtagToTaskMeta tasks = new TaskHashtagToTaskMeta();
}
