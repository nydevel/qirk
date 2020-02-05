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
package org.wrkr.clb.services.dto.project.imprt.jira;

import java.util.List;

import org.wrkr.clb.common.util.datetime.DateTimeWithEpochDTO;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JiraUploadDTO {

    public DateTimeWithEpochDTO timestamp;
    @JsonProperty(value = "archive_filename")
    public String archiveFilename;
    @JsonProperty(value = "imported_projects")
    public List<ImportedJiraProject> importedProjects;

    public JiraUploadDTO(long timestamp, String archiveName, List<ImportedJiraProject> importedProjects) {
        this.timestamp = new DateTimeWithEpochDTO(timestamp);
        this.archiveFilename = archiveName;
        this.importedProjects = importedProjects;
    }
}
