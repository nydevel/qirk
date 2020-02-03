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
package org.wrkr.clb.services.organization;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.NameAndUiIdDTO;
import org.wrkr.clb.services.dto.organization.OrganizationDTO;
import org.wrkr.clb.services.dto.organization.OrganizationReadDTO;


@Validated
public interface OrganizationService {

    public ExistsDTO checkUiId(User currentUser,
            @NotNull(message = "uiId in OrganizationService must not be null") @Pattern(regexp = RegExpPattern.SLUG
                    + "+", message = "uiId in OrganizationService must be slug") String uiId);

    @Validated(OnCreate.class)
    public Organization create(User currentUser, @Valid OrganizationDTO organizationDTO) throws Exception;

    @Validated(OnCreate.class)
    public Organization createPredefinedForUser(User creatorUser, @Valid OrganizationDTO organizationDTO) throws Exception;

    @Validated(OnUpdate.class)
    public OrganizationReadDTO update(User currentUser,
            @Valid OrganizationDTO organizationDTO) throws Exception;

    // public OrganizationReadDTO addDropbox(User currentUser, @Valid OAuthCodeDTO codeDTO) throws Exception;

    // public OrganizationReadDTO removeDropbox(User currentUser, @Valid RecordVersionDTO dto) throws Exception;

    public OrganizationReadDTO get(User currentUser,
            @NotNull(message = "id in OrganizationService must not be null") Long id,
            boolean includeCanLeave) throws Exception;

    public OrganizationReadDTO getByUiId(User currentUser,
            @NotNull(message = "uiId in OrganizationService must not be null") String uiId,
            boolean includeCanLeave) throws Exception;

    public List<NameAndUiIdDTO> listByUser(User currentUser, boolean canCreateProject);
}
