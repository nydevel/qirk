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
package org.wrkr.clb.services.dto.organization;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.user.PublicUserDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrganizationMemberReadDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    public Long recordVersion;

    public PublicUserDTO user;

    public Boolean enabled;

    public Boolean manager;

    @JsonInclude(Include.NON_NULL)
    public Boolean owner;

    public static OrganizationMemberReadDTO fromEntity(OrganizationMember organizationMember) {
        OrganizationMemberReadDTO dto = new OrganizationMemberReadDTO();

        dto.id = organizationMember.getId();
        dto.recordVersion = organizationMember.getRecordVersion();
        dto.user = PublicUserDTO.fromEntity(organizationMember.getUser());
        dto.enabled = organizationMember.isEnabled();
        dto.manager = organizationMember.isManager();

        return dto;
    }

    public static OrganizationMemberReadDTO fromEntity(OrganizationMember organizationMember, Long ownerUserId) {
        OrganizationMemberReadDTO dto = fromEntity(organizationMember);
        dto.owner = organizationMember.getUser().getId().equals(ownerUserId);
        return dto;
    }

    public static List<OrganizationMemberReadDTO> fromEntities(
            List<OrganizationMember> organizationMemberList, Long ownerUserId) {
        List<OrganizationMemberReadDTO> dtoList = new ArrayList<OrganizationMemberReadDTO>(organizationMemberList.size());
        for (OrganizationMember organizationMember : organizationMemberList) {
            dtoList.add(fromEntity(organizationMember, ownerUserId));
        }
        return dtoList;
    }
}
