/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.model.project.imprt.jira;

import org.wrkr.clb.model.BaseEntityMeta;

public class ImportedJiraProjectMeta extends BaseEntityMeta {

    public static final String TABLE_NAME = "imported_jira_project";

    public static final String organizationId = "organization_id";
    public static final String projectId = "project_id";
    public static final String uploadTimestamp = "upload_timestamp";
    public static final String jiraProjectId = "jira_project_id";
    public static final String jiraProjectKey = "jira_project_key";
    public static final String jiraProjectName = "jira_project_name";
    public static final String updatedAt = "updated_at";
}
