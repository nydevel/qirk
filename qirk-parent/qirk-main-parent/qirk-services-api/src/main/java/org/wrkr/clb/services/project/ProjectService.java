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
package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.RecordVersionDTO;
import org.wrkr.clb.services.dto.project.ProjectDTO;
import org.wrkr.clb.services.dto.project.ProjectDocDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteOptionDTO;
import org.wrkr.clb.services.dto.project.ProjectNameAndUiIdDTO;
import org.wrkr.clb.services.dto.project.ProjectReadDTO;
import org.wrkr.clb.services.dto.project.ProjectWithOrganizationDTO;

@Validated
public interface ProjectService {

    public ExistsDTO checkUiId(User currentUser,
            @NotNull(message = "uiId must not be null") @Pattern(regexp = RegExpPattern.SLUG
                    + "+", message = "uiId must be slug") String uiId)
            throws Exception;

    public Project create(User creatorUser, @Valid ProjectDTO projectDTO,
            @NotNull(message = "membersToCreate must not be null") List<User> membersToCreate) throws Exception;

    @Validated(OnCreate.class)
    public ProjectReadDTO create(User currentUser, @Valid ProjectDTO projectDTO) throws Exception;

    @Validated(OnUpdate.class)
    public ProjectReadDTO update(User currentUser, @Valid ProjectDTO projectDTO) throws Exception;

    public ProjectDocDTO getDocumentation(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;

    public ProjectDocDTO getDocumentationByUiId(User currentUser,
            @NotNull(message = "uiId must not be null") String uiId) throws Exception;

    public ProjectReadDTO updateDocumentation(User currentUser, @Valid ProjectDocDTO documentationDTO) throws Exception;

    public void makePublic(User currentUser, @Valid RecordVersionDTO projectDTO) throws Exception;

    // public ProjectReadDTO addDropbox(User currentUser, @Valid OAuthCodeDTO codeDTO) throws Exception;

    // public ProjectDropboxDTO getDropbox(User currentUser, @NotNull(message = "id must not be null") Long id);

    // public ProjectDropboxDTO getDropboxByUiId(User currentUser, @NotNull(message = "uiId must not be null")
    // String uiId);

    // public ProjectReadDTO removeDropbox(User currentUser, @Valid RecordVersionDTO projectDTO) throws Exception;

    public ProjectReadDTO get(User currentUser,
            @NotNull(message = "id must not be null") Long id,
            boolean includeApplication) throws Exception;

    public ProjectReadDTO getByUiId(User currentUser,
            @NotNull(message = "uiId must not be null") String uiId,
            boolean includeApplication) throws Exception;

    public List<ProjectWithOrganizationDTO> listManagedByUser(User currentUser);

    public List<ProjectWithOrganizationDTO> listByUser(User currentUser);

    @Deprecated
    public List<ProjectInviteOptionDTO> listInviteOptions(User currentUser, long userId);

    public List<ProjectNameAndUiIdDTO> listAvailableToUser(User user);

    public ChatPermissionsDTO getChatToken(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;
}
