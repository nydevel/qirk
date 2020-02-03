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
package org.wrkr.clb.services.organization.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.LanguageRepo;
import org.wrkr.clb.repo.organization.OrganizationMemberRepo;
import org.wrkr.clb.repo.organization.OrganizationRepo;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.NameAndUiIdDTO;
import org.wrkr.clb.services.dto.organization.OrganizationDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberDTO;
import org.wrkr.clb.services.dto.organization.OrganizationReadDTO;
import org.wrkr.clb.services.organization.OrganizationMemberService;
import org.wrkr.clb.services.organization.OrganizationService;
import org.wrkr.clb.services.project.ProjectService;
import org.wrkr.clb.services.security.OrganizationSecurityService;
import org.wrkr.clb.services.security.SecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.ConflictException;
import org.wrkr.clb.services.util.exception.NotFoundException;


@Validated
@Service
public class DefaultOrganizationService implements OrganizationService {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DefaultOrganizationService.class);

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private OrganizationMemberRepo organizationMemberRepo;

    @Autowired
    private OrganizationMemberService organizationMemberService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OrganizationSecurityService securityService;

    @Autowired
    private SecurityService authnSecurityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public ExistsDTO checkUiId(User currentUser, String uiId) {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        return new ExistsDTO(organizationRepo.existsByUiId(uiId.toLowerCase()));
    }

    private Organization create(User creatorUser, OrganizationDTO organizationDTO, boolean predefinedForUser)
            throws Exception {
        if (creatorUser == null) {
            throw new NotFoundException("Creator user");
        }

        Organization organization = new Organization();
        organization.setOwner(creatorUser);
        organization.setPredefinedForUser(predefinedForUser);
        organization.setName(organizationDTO.name);

        String uiId = organizationDTO.uiId.strip().toLowerCase();
        if (uiId.isEmpty()) {
            do {
                uiId = RandomStringUtils.randomAlphanumeric(Organization.UI_ID_LENGTH).toLowerCase();
            } while (organizationRepo.existsByUiId(uiId));
        }
        organization.setUiId(uiId);

        List<Language> languages = languageRepo.listByIds(organizationDTO.languageIds);
        organization.setLanguages(languages);

        organizationRepo.persist(organization);

        OrganizationMemberDTO organizationMemberDTO = new OrganizationMemberDTO();
        organizationMemberDTO.enabled = true;
        organizationMemberDTO.manager = true;
        OrganizationMember owner = organizationMemberService.create(
                creatorUser, organization, organizationMemberDTO, !predefinedForUser);
        organization.setMembers(Arrays.asList(owner));

        return organization;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public Organization create(User currentUser, OrganizationDTO organizationDTO) throws Exception {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish
        return create(currentUser, organizationDTO, false);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public Organization createPredefinedForUser(User creatorUser, OrganizationDTO organizationDTO) throws Exception {
        return create(creatorUser, organizationDTO, true);
    }

    private Organization checkRecordVersion(Organization organization, Long recordVersion) throws ConflictException {
        if (!organization.getRecordVersion().equals(recordVersion)) {
            throw new ConflictException("Concurrent modification occured.");
        }
        organization.setRecordVersion(organization.getRecordVersion() + 1);
        return organization;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public OrganizationReadDTO update(User currentUser, OrganizationDTO organizationDTO) throws ApplicationException {
        // security start
        securityService.authzCanModifyOrganization(currentUser, organizationDTO.id);
        // security finish

        Organization organization = organizationRepo.getAndFetchDropboxSettings(organizationDTO.id);
        if (organization == null) {
            throw new NotFoundException("Organization");
        }
        organization = checkRecordVersion(organization, organizationDTO.recordVersion);

        organization.setName(organizationDTO.name);
        if (!organizationDTO.uiId.isBlank()) {
            organization.setUiId(organizationDTO.uiId.strip().toLowerCase());
        }

        List<Language> languages = languageRepo.listByIds(organizationDTO.languageIds);
        organization.setLanguages(languages);

        organization = organizationRepo.merge(organization);

        return OrganizationReadDTO.fromEntityWithLanguages(organization);
    }

    /*@formatter:off
    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public OrganizationReadDTO addDropbox(User currentUser, OAuthCodeDTO codeDTO) throws Exception {
        // security start
        securityService.authzCanModifyOrganization(currentUser, codeDTO.id);
        // security finish

        Organization organization = organizationRepo.get(codeDTO.id);
        if (organization == null) {
            throw new NotFoundException("Organization");
        }
        organization = checkRecordVersion(organization, codeDTO.recordVersion);

        String token = dropboxService.getToken(codeDTO.code, dropboxService.getRedirectURIForOrganization());
        DropboxSettings dropboxSettings = new DropboxSettings();
        dropboxSettings.setToken(token);
        dropboxSettingsRepo.persist(dropboxSettings);

        organization.setDropboxSettings(dropboxSettings);
        organization = organizationRepo.merge(organization);
        return OrganizationReadDTO.fromEntity(organization);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public OrganizationReadDTO removeDropbox(User currentUser, RecordVersionDTO dto) throws ApplicationException {
        // security start
        securityService.authzCanModifyOrganization(currentUser, dto.id);
        // security finish

        Organization organization = organizationRepo.get(dto.id);
        if (organization == null) {
            throw new NotFoundException("Organization");
        }
        organization = checkRecordVersion(organization, dto.recordVersion);

        organization.setDropboxSettings(null);
        organization = organizationRepo.merge(organization);
        return OrganizationReadDTO.fromEntity(organization);
    }
    @formatter:on*/

    private OrganizationReadDTO getDTOWithProjects(User currentUser, Organization organization, boolean includeCanLeave)
            throws ApplicationException {
        if (organization == null) {
            throw new NotFoundException("Organization");
        }

        OrganizationMember currentMember = null;
        if (currentUser != null) {
            currentMember = organizationMemberRepo.getNotFiredByUserAndOrganization(currentUser, organization);
            if (currentMember != null) {
                currentMember.setUser(currentUser);
            }
        }

        OrganizationReadDTO organizationDTO = OrganizationReadDTO.fromEntityWithEverythingForRead(organization, currentMember);
        organizationDTO.projects = projectService.listAvailableToMemberByOrganization(
                organization, currentMember);
        if (includeCanLeave) {
            organizationDTO.canLeave = (currentMember != null && !currentUser.getId().equals(organization.getOwner().getId()));
        }
        return organizationDTO;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public OrganizationReadDTO get(User currentUser, Long id, boolean includeCanLeave) throws ApplicationException {
        // security start
        securityService.authzCanReadOrganization(currentUser, id);
        // security finish

        Organization organization = organizationRepo.getAndFetchOwnerAndDropboxSettingsAndLanguages(id);
        return getDTOWithProjects(currentUser, organization, includeCanLeave);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public OrganizationReadDTO getByUiId(User currentUser, String uiId, boolean includeCanLeave) throws ApplicationException {
        // security start
        securityService.authzCanReadOrganization(currentUser, uiId);
        // security finish

        Organization organization = organizationRepo.getByUiId(uiId, true);
        return getDTOWithProjects(currentUser, organization, includeCanLeave);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<NameAndUiIdDTO> listByUser(User currentUser, boolean canCreateProject) {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        List<Organization> organizationList = organizationRepo.listByNotFiredMemberUserAndFetchOwner(
                currentUser, canCreateProject);

        // set predefined organization first
        Organization predefinedOrganization = null;
        for (int i = 1; i < organizationList.size(); i++) {
            Organization organization = organizationList.get(i);
            if (!organization.getPredefinedForUser()) {
                continue;
            }
            User predefinedForUser = organization.getOwner();
            if (predefinedForUser != null && predefinedForUser.getId().equals(currentUser.getId())) {
                predefinedOrganization = organization;
                organizationList.remove(i);
                break;
            }
        }

        return NameAndUiIdDTO.fromOrganizations(predefinedOrganization, organizationList);
    }
}
