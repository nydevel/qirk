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
package org.wrkr.clb.services.security;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;

@Validated
public interface OrganizationSecurityService {

    public void authzCanReadOrganization(User user,
            @NotNull(message = "organizationId is null") Long organizationId) throws SecurityException;

    public void authzCanReadOrganization(User user,
            @NotNull(message = "organizationUiId is null") String organizationUiId) throws SecurityException;

    public void authzCanReadOrganization(User user, @Valid IdOrUiIdDTO organizationDTO) throws SecurityException;

    public void authzCanModifyOrganization(User user,
            @NotNull(message = "organizationId is null") Long organizationId) throws SecurityException;

    public Organization authzCanReadOrganizationMembers(User user,
            @NotNull(message = "organizationId is null") Long organizationId) throws SecurityException;

    public Organization authzCanReadOrganizationMembers(User user,
            @NotNull(message = "organizationUiId is null") String organizationUiId) throws SecurityException;

    public Organization authzCanReadOrganizationMembers(User user,
            @Valid IdOrUiIdDTO organizationDTO) throws SecurityException;

    public void authzCanModifyOrganizationMembers(User user,
            @NotNull(message = "organizationId is null") Long organizationId) throws SecurityException;

    public void authzCanModifyOrganizationMembers(User user,
            @Valid IdOrUiIdDTO organizationDTO) throws SecurityException;

    public Organization authzCanReadOrganizationMember(User user,
            @NotNull(message = "organizationMemberId is null") Long organizationMemberId) throws SecurityException;

    public void authzCanModifyOrganizationMember(User user,
            @NotNull(message = "organizationMemberId is null") Long organizationMemberId) throws SecurityException;

    public void authzCanImportProjects(User user,
            @NotNull(message = "organizationId is null") Long organizationId) throws SecurityException;
}
