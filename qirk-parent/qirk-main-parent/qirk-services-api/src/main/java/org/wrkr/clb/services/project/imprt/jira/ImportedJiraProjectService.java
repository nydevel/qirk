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
package org.wrkr.clb.services.project.imprt.jira;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProject;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.project.imprt.ImportStatusDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectImportDTO;

public interface ImportedJiraProjectService {

    public Map<Long, List<ImportedJiraProject>> mapTimestampToImportedProject(long organizationId);

    public void importNewProject(User importingUser,
            Document entitiesDoc, ImportedJiraProject importedProject, JiraProjectImportDTO importDTO,
            String uploadFolderPath, char uploadFolderDelimeter) throws Exception;

    public ImportStatusDTO importProjectUpdate(User importingUser,
            Document entitiesDoc, ImportedJiraProject importedProject, JiraProjectImportDTO importDTO,
            String uploadFolderPath, char uploadFolderDelimeter) throws Exception;
}
