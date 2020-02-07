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
package org.wrkr.clb.services.dto.project;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.GrantedPermissionsProjectInvite;
import org.wrkr.clb.model.project.InviteStatus;
import org.wrkr.clb.model.project.ProjectInviteToken;
import org.wrkr.clb.services.dto.user.PublicUserDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Evgeny Poreykin
 *
 */
public class GrantedPermissionsProjectInviteReadDTO extends ProjectInviteReadDTO {

    @JsonInclude(Include.NON_NULL)
    public String email;

    @JsonProperty(value = "write_allowed")
    public Boolean writeAllowed;

    public Boolean manager;

    @JsonInclude(Include.NON_NULL)
    public PublicUserDTO sender;

    @JsonProperty(value = "email_sent")
    @JsonInclude(Include.NON_NULL)
    public Boolean emailSent;

    public static GrantedPermissionsProjectInviteReadDTO fromGrantedPermissionsInvite(GrantedPermissionsProjectInvite invite,
            boolean includeUser, boolean includeProject, boolean includeStatus, boolean includeSender) {
        GrantedPermissionsProjectInviteReadDTO dto = new GrantedPermissionsProjectInviteReadDTO();

        dto.setFields(invite, includeUser, includeProject, includeStatus);
        if (includeSender) {
            dto.sender = PublicUserDTO.fromEntity(invite.getSender());
        }

        return dto;
    }

    public static GrantedPermissionsProjectInviteReadDTO fromGrantedPermissionsInvite(GrantedPermissionsProjectInvite invite) {
        return fromGrantedPermissionsInvite(invite, false, false, false, false);
    }

    public static GrantedPermissionsProjectInviteReadDTO fromGrantedPermissionsInviteForProject(
            GrantedPermissionsProjectInvite invite) {
        return fromGrantedPermissionsInvite(invite, true, false, true, true);
    }

    public static GrantedPermissionsProjectInviteReadDTO fromGrantedPermissionsInviteForProject(
            GrantedPermissionsProjectInvite invite, ProjectInviteToken inviteToken, boolean emailSent) {
        GrantedPermissionsProjectInviteReadDTO dto = fromGrantedPermissionsInviteForProject(invite);
        dto.email = inviteToken.getEmailAddress();
        dto.emailSent = emailSent;
        return dto;
    }

    public static GrantedPermissionsProjectInviteReadDTO fromGrantedPermissionsInviteWithUserAndProject(
            GrantedPermissionsProjectInvite invite) {
        return fromGrantedPermissionsInvite(invite, true, true, false, false);
    }

    public static List<GrantedPermissionsProjectInviteReadDTO> fromGrantedPermissionsInvites(
            List<GrantedPermissionsProjectInvite> inviteList,
            boolean includeUser, boolean includeProject, InviteStatus status, boolean includeSender) {
        List<GrantedPermissionsProjectInviteReadDTO> dtoList = new ArrayList<GrantedPermissionsProjectInviteReadDTO>();
        for (GrantedPermissionsProjectInvite invite : inviteList) {
            GrantedPermissionsProjectInviteReadDTO dto = fromGrantedPermissionsInvite(invite,
                    includeUser, includeProject, false, includeSender);
            dto.status = status;
            dtoList.add(dto);
        }
        return dtoList;
    }

    public static List<GrantedPermissionsProjectInviteReadDTO> fromGrantedPermissionsInvitesForUser(
            List<GrantedPermissionsProjectInvite> inviteList) {
        return fromGrantedPermissionsInvites(inviteList, false, true, null, true);
    }

    public static List<GrantedPermissionsProjectInviteReadDTO> fromGrantedPermissionsInvitesForProject(
            List<GrantedPermissionsProjectInvite> inviteList, InviteStatus status) {
        return fromGrantedPermissionsInvites(inviteList, true, false, status, true);
    }

    protected void setFields(GrantedPermissionsProjectInvite invite,
            boolean includeUser, boolean includeProject, boolean includeStatus) {
        super.setFields(invite, includeUser, includeProject, includeStatus);

        writeAllowed = invite.isWriteAllowed();
        manager = invite.isManager();

        if (includeUser) {
            email = (invite.getToken() == null ? "" : invite.getToken().getEmailAddress());
        }
    }
}
