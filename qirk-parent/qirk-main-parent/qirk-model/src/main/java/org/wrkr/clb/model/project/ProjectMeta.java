/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.model.project;

import org.wrkr.clb.model.BaseVersionedEntityMeta;

public class ProjectMeta extends BaseVersionedEntityMeta {

    public static final String TABLE_NAME = "project";

    public static final String ownerId = "user_owner_id";
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

    public static final ProjectToTagMeta tags = new ProjectToTagMeta();
    public static final ProjectToLanguageMeta languages = new ProjectToLanguageMeta();

    public static final ProjectMeta DEFAULT = new ProjectMeta(TABLE_NAME);

    public ProjectMeta(String tableAlias) {
        super(tableAlias);
    };
}
