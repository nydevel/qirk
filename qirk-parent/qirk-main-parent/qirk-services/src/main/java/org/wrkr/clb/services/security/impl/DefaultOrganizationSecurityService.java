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
package org.wrkr.clb.services.security.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.security.OrganizationSecurityService;

@Validated
@Service
public class DefaultOrganizationSecurityService extends BaseOrganizationSecurityService implements OrganizationSecurityService {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DefaultOrganizationSecurityService.class);

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadOrganization(User user, Long organizationId) throws SecurityException {
        Organization organization = getOrganizationWithMemberByUserAndOrganizationId(user, organizationId);
        authzCanReadOrganization(user, organization);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadOrganization(User user, String organizationUiId) throws SecurityException {
        Organization organization = getOrganizationWithMemberByUserAndOrganizationUiId(user, organizationUiId);
        authzCanReadOrganization(user, organization);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanReadOrganization(User user, IdOrUiIdDTO organizationDTO) throws SecurityException {
        Organization organization = getOrganizationWithMemberByUserAndOrganizationIdOrUiId(user, organizationDTO);
        authzCanReadOrganization(user, organization);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyOrganization(User user, Long organizationId) throws SecurityException {
        requireAuthnOrThrowException(user);

        OrganizationMember member = getOrganizationMemberByUserAndOrganizationId(user, organizationId);
        requireOrganizationManagerOrThrowException(member);
    }

    /**
     * 
     * @return organization to read
     */
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Organization authzCanReadOrganizationMembers(User user, Long organizationId) throws SecurityException {
        Organization organization = getOrganizationWithMemberByUserAndOrganizationId(user, organizationId);
        return authzCanReadOrganizationMembers(user, organization);
    }

    /**
     * 
     * @return organization to read
     */
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Organization authzCanReadOrganizationMembers(User user, String organizationUiId) throws SecurityException {
        Organization organization = getOrganizationWithMemberByUserAndOrganizationUiId(user, organizationUiId);
        return authzCanReadOrganizationMembers(user, organization);
    }

    /**
     * 
     * @return organization to read
     */
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Organization authzCanReadOrganizationMembers(User user, IdOrUiIdDTO organizationDTO) throws SecurityException {
        Organization organization = getOrganizationWithMemberByUserAndOrganizationIdOrUiId(user, organizationDTO);
        return authzCanReadOrganizationMembers(user, organization);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyOrganizationMembers(User user, Long organizationId) throws SecurityException {
        requireAuthnOrThrowException(user);

        OrganizationMember member = getOrganizationMemberByUserAndOrganizationId(user, organizationId);
        requireOrganizationManagerOrThrowException(member);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyOrganizationMembers(User user, IdOrUiIdDTO organizationDTO) throws SecurityException {
        requireAuthnOrThrowException(user);

        OrganizationMember member = getOrganizationMemberByUserAndOrganizationIdOrUiId(user, organizationDTO);
        requireOrganizationManagerOrThrowException(member);
    }

    /**
     * 
     * @return organization to read
     */
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Organization authzCanReadOrganizationMember(User user, Long organizationMemberId) throws SecurityException {
        Organization organization = getOrganizationWithMemberByUserAndOtherMemberId(user, organizationMemberId);
        authzCanReadOrganization(user, organization);
        return organization;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanModifyOrganizationMember(User user, Long organizationMemberId) throws SecurityException {
        requireAuthnOrThrowException(user);

        OrganizationMember modifiedMember = getOrganizationMemberWithOrganizationByIdAndCurrentMemberByUser(
                user, organizationMemberId);
        if (modifiedMember == null) {
            return;
        }

        Organization organization = modifiedMember.getOrganization();
        List<OrganizationMember> currentMembership = organization.getMembers();
        if (modifiedMember.getUserId().equals(organization.getOwnerId())) {
            requireOrganizationOwnerOrThrowException(currentMembership, user);
        }
        requireOrganizationManagerOrThrowException(currentMembership, user);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void authzCanImportProjects(User user, Long organizationId) throws SecurityException {
        requireAuthnOrThrowException(user);

        Organization organization = getOrganizationWithMemberByUserAndOrganizationId(user, organizationId);
        requireOrganizationOwnerOrThrowException(organization.getMembers(), user);
    }
}
