package org.wrkr.clb.model.project;

import org.wrkr.clb.model.BaseVersionedEntityMeta;

public class ProjectMeta extends BaseVersionedEntityMeta {

    public static final String TABLE_NAME = "project";

    public static final String ownerId = "owner_user_id";
    public static final String taskNumberSequenceId = "task_number_sequence_id";
    public static final String name = "name";
    public static final String uiId = "ui_id";
    public static final String key = "key";
    public static final String isPrivate = "private";
    public static final String descriptionMd = "description_md";
    public static final String descriptionHtml = "description_html";
    public static final String documentationMd = "documentation_md";
    public static final String documentationHtml = "documentation_html";
    public static final String frozen = "frozen";
    public static final String dropboxSettingsId = "dropbox_settings_id";
    public static final String externalRepoId = "external_repo_id";

    public static final ProjectMeta DEFAULT = new ProjectMeta(TABLE_NAME);

    public ProjectMeta(String tableAlias) {
        super(tableAlias);
    };
}
