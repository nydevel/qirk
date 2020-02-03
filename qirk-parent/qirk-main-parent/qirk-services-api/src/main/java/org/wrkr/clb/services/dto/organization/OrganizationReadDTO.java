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

import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.services.dto.DropboxSettingsDTO;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.NameAndUiIdDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrganizationReadDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    public Long recordVersion;

    public String name;

    @JsonProperty(value = "ui_id")
    public String uiId;

    @JsonProperty(value = "private")
    public Boolean isPrivate;

    @Deprecated
    @JsonProperty(value = "dropbox_settings")
    public DropboxSettingsDTO dropboxSettings;

    @JsonInclude(Include.NON_NULL)
    public List<Long> languages;

    @JsonProperty(value = "is_member")
    @JsonInclude(Include.NON_NULL)
    public Boolean isMember;

    @JsonProperty(value = "can_manage")
    @JsonInclude(Include.NON_NULL)
    public Boolean canManage;

    @JsonProperty(value = "is_owner")
    @JsonInclude(Include.NON_NULL)
    public Boolean isOwner;

    @JsonProperty(value = "can_leave")
    @JsonInclude(Include.NON_NULL)
    public Boolean canLeave;

    @JsonInclude(Include.NON_NULL)
    public List<NameAndUiIdDTO> projects;

    public static OrganizationReadDTO fromEntity(Organization organization) {
        OrganizationReadDTO dto = new OrganizationReadDTO();

        dto.id = organization.getId();
        dto.recordVersion = organization.getRecordVersion();
        dto.name = organization.getName();
        dto.uiId = organization.getUiId();
        dto.isPrivate = organization.isPrivate();

        return dto;
    }

    public static OrganizationReadDTO fromEntityWithLanguages(Organization organization) {
        OrganizationReadDTO dto = fromEntity(organization);

        dto.languages = new ArrayList<Long>();
        for (Language language : organization.getLanguages()) {
            dto.languages.add(language.getId());
        }

        return dto;
    }

    public static OrganizationReadDTO fromEntityWithEverythingForRead(
            Organization organization, OrganizationMember currentMember) {
        OrganizationReadDTO dto = fromEntityWithLanguages(organization);

        dto.isMember = (currentMember != null);
        if (currentMember != null) {
            dto.canManage = currentMember.isManager();
            dto.isOwner = currentMember.getUser().getId().equals(organization.getOwnerId());
        }

        return dto;
    }
}
