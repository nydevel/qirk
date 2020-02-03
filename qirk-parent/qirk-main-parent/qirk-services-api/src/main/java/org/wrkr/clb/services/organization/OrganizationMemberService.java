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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberReadDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberUserDTO;

@Validated
public interface OrganizationMemberService {

    public OrganizationMember create(
            @NotNull(message = "user in OrganizationMemberService must not be null") User user,
            @NotNull(message = "organization in OrganizationMemberService must not be null") Organization organization,
            @Valid OrganizationMemberDTO organizationMemberDTO, boolean saveToElasticsearch);

    @Validated(OnCreate.class)
    public OrganizationMember create(User currentUser,
            @Valid OrganizationMemberDTO organizationMemberDTO)
            throws Exception;

    @Validated(OnUpdate.class)
    public OrganizationMember update(User currentUser,
            @Valid OrganizationMemberDTO organizationMemberDTO)
            throws Exception;

    public OrganizationMemberReadDTO get(User currentUser,
            @NotNull(message = "id in OrganizationMemberService must not be null") Long id) throws Exception;

    public void delete(User currentUser,
            @NotNull(message = "organizationMemberId in OrganizationMemberService must not be null") Long organizationMemberId)
            throws Exception;

    public void leave(User currentUser,
            @NotNull(message = "organizationDTO in OrganizationMemberService must not be null") @Valid IdOrUiIdDTO organizationDTO)
            throws Exception;

    public List<OrganizationMemberUserDTO> listByMemberIds(User currentUser,
            @NotNull(message = "organizationId in OrganizationMemberService must not be null") Long organizationId,
            @NotEmpty(message = "ids in OrganizationMemberService must not be empty") List<Long> memberIds);

    public List<OrganizationMemberReadDTO> listByOrganizationId(User currentUser,
            @NotNull(message = "organizationId in OrganizationMemberService must not be null") Long organizationId);

    public List<OrganizationMemberReadDTO> listByOrganizationUiId(User currentUser,
            @NotNull(message = "organizationUiId in OrganizationMemberService must not be null") String organizationUiId);

    public List<OrganizationMemberUserDTO> search(User currentUser,
            @NotNull(message = "prefix in OrganizationMemberService must not be null") String prefix,
            @Valid IdOrUiIdDTO organizationDTO, boolean meFirst)
            throws Exception;
}
