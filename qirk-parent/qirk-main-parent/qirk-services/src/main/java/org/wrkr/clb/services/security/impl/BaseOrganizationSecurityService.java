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
package org.wrkr.clb.services.security.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.organization.JDBCOrganizationMemberRepo;
import org.wrkr.clb.repo.security.SecurityOrganizationRepo;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;

public abstract class BaseOrganizationSecurityService extends BaseSecurityService {

    @Autowired
    private SecurityOrganizationRepo organizationRepo;

    @Autowired
    private JDBCOrganizationMemberRepo organizationMemberRepo;

    protected Organization getOrganizationWithMemberByUserAndOrganizationId(User user, Long organizationId) {
        if (user == null) {
            return organizationRepo.getById(organizationId);
        }
        return organizationRepo.getByIdAndFetchNotFiredOrganizationMemberByUserId(organizationId, user.getId());
    }

    protected Organization getOrganizationWithMemberByUserAndOrganizationUiId(User user, String organizationUiId) {
        if (user == null) {
            return organizationRepo.getByUiId(organizationUiId);
        }
        return organizationRepo.getByUiIdAndFetchNotFiredOrganizationMemberByUserId(organizationUiId, user.getId());
    }

    protected Organization getOrganizationWithMemberByUserAndOrganizationIdOrUiId(User user, IdOrUiIdDTO organizationDTO) {
        if (organizationDTO.id != null) {
            return getOrganizationWithMemberByUserAndOrganizationId(user, organizationDTO.id);
        }
        if (organizationDTO.uiId != null) {
            return getOrganizationWithMemberByUserAndOrganizationUiId(user, organizationDTO.uiId);
        }
        return null;
    }

    protected Organization getOrganizationWithMemberByUserAndOtherMemberId(User user, Long organizationMemberId) {
        if (user == null) {
            return organizationRepo.getByMemberId(organizationMemberId);
        }
        return organizationRepo.getByOtherMemberIdAndFetchNotFiredOrganizationMemberByUserId(organizationMemberId, user.getId());
    }

    protected OrganizationMember getOrganizationMemberByUserAndOrganizationId(User user, Long organizationId) {
        return organizationMemberRepo.getNotFiredByUserIdAndOrganizationId(user.getId(), organizationId);
    }

    private OrganizationMember getOrganizationMemberByUserAndOrganizationUiId(User user, String organizationUiId) {
        return organizationMemberRepo.getNotFiredByUserIdAndOrganizationUiId(user.getId(), organizationUiId);
    }

    protected OrganizationMember getOrganizationMemberByUserAndOrganizationIdOrUiId(User user, IdOrUiIdDTO organizationDTO) {
        if (organizationDTO.id != null) {
            return getOrganizationMemberByUserAndOrganizationId(user, organizationDTO.id);
        }
        if (organizationDTO.uiId != null) {
            return getOrganizationMemberByUserAndOrganizationUiId(user, organizationDTO.uiId);
        }
        return null;
    }

    protected OrganizationMember getOrganizationMemberWithOrganizationByIdAndCurrentMemberByUser(User currentUser,
            Long memberId) {
        if (currentUser == null) {
            return organizationMemberRepo.getNotFiredByIdAndFetchOrganization(memberId);
        }
        return organizationMemberRepo.getNotFiredByIdAndFetchOrganizationAndOtherMemberByUserId(memberId, currentUser.getId());
    }

    private void requireOrganizationMemberOrThrowException(OrganizationMember member) {
        if (member == null || !member.isEnabled() || member.isFired()) {
            throw new SecurityException(
                    "User " + member.getUserId() + " is not a member of the organization " + member.getOrganizationId());
        }
    }

    protected OrganizationMember requireOrganizationMemberOrThrowException(List<OrganizationMember> members, User user) {
        for (OrganizationMember member : members) {
            if (member == null || !member.getUserId().equals(user.getId())) {
                continue;
            }
            requireOrganizationMemberOrThrowException(member);
            return member;
        }
        throw new SecurityException("User " + user + " is not a member of the organization");
    }

    protected void requireOrganizationManagerOrThrowException(OrganizationMember member) throws SecurityException {
        requireOrganizationMemberOrThrowException(member);
        if (!member.isManager()) {
            throw new SecurityException(
                    "User " + member.getUserId() + " is not a manager of the organization" + member.getOrganizationId());
        }
    }

    protected OrganizationMember requireOrganizationManagerOrThrowException(List<OrganizationMember> members, User user) {
        for (OrganizationMember member : members) {
            if (member == null || !member.getUserId().equals(user.getId())) {
                continue;
            }
            requireOrganizationManagerOrThrowException(member);
            return member;
        }
        throw new SecurityException("User " + user + " is not a member of the organization");
    }

    protected void requireOrganizationOwnerOrThrowException(OrganizationMember member) throws SecurityException {
        requireOrganizationMemberOrThrowException(member);
        if (!member.getUserId().equals(member.getOrganization().getOwnerId())) {
            throw new SecurityException(
                    "User " + member.getUserId() + " is not an owner of the organization " + member.getOrganization());
        }
    }

    protected OrganizationMember requireOrganizationOwnerOrThrowException(List<OrganizationMember> members, User user) {
        for (OrganizationMember member : members) {
            if (member == null || !member.getUserId().equals(user.getId())) {
                continue;
            }
            requireOrganizationOwnerOrThrowException(member);
            return member;
        }
        throw new SecurityException("User " + user + " is not an owner of the organization");
    }

    protected void authzCanReadOrganization(User user, Organization organization) throws SecurityException {
        if (organization == null || !organization.isPrivate()) {
            return; // public can be read by everyone
        }
        requireAuthnOrThrowException(user);

        List<OrganizationMember> members = organization.getMembers();
        requireOrganizationMemberOrThrowException(members, user);
        // members can read
    }

    /**
     * 
     * @return organization to read
     */
    protected Organization authzCanReadOrganizationMembers(User user, Organization organization) throws SecurityException {
        authzCanReadOrganization(user, organization);
        return organization;
    }
}
