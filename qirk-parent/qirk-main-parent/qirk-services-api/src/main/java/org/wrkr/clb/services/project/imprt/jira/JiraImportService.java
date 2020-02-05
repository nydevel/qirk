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
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.fileupload.FileItem;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.project.imprt.ImportStatusDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraOrganizationMatchDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectImportDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraUploadDTO;


@Validated
public interface JiraImportService {

    public JiraUploadDTO uploadJiraImportFile(User currentUser,
            @NotNull(message = "file in ProjectImportService must not be null") FileItem file,
            @NotNull(message = "organizationId in ProjectImportService must not be null") Long organizationId) throws Exception;

    public List<JiraUploadDTO> listUploads(User currentUser, long organizationId) throws Exception;

    public List<JiraProjectDTO> listProjects(User currentUser, long organizationId, long timestamp) throws Exception;

    public JiraOrganizationMatchDTO listProjectsData(User currentUser, long organizationId, long timestamp,
            @NotEmpty(message = "projectIds in ProjectImportService must not be empty") Set<String> projectIds) throws Exception;

    public List<ImportStatusDTO> importProjects(User currentUser, @Valid JiraProjectImportDTO importDTO) throws Exception;
}
