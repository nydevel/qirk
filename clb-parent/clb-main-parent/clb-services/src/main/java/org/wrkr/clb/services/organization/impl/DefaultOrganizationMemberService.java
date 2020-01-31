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

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.organization.JDBCOrganizationMemberRepo;
import org.wrkr.clb.repo.organization.OrganizationMemberRepo;
import org.wrkr.clb.repo.organization.OrganizationRepo;
import org.wrkr.clb.repo.project.task.TaskSubscriberRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberReadDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberUserDTO;
import org.wrkr.clb.services.impl.BaseVersionedEntityService;
import org.wrkr.clb.services.organization.OrganizationMemberService;
import org.wrkr.clb.services.project.ProjectMemberService;
import org.wrkr.clb.services.security.OrganizationSecurityService;
import org.wrkr.clb.services.security.SecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.NotFoundException;

@Validated
@Service
public class DefaultOrganizationMemberService extends BaseVersionedEntityService implements OrganizationMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultOrganizationMemberService.class);

    @Autowired
    private OrganizationMemberRepo orgMemberRepo;

    @Autowired
    private JDBCOrganizationMemberRepo jdbcOrgMemberRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TaskSubscriberRepo taskSubscriberRepo;

    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private ElasticsearchUserService elasticsearchService;

    @Autowired
    private OrganizationSecurityService securityService;

    @Autowired
    private SecurityService authnSecurityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public OrganizationMember create(
            User user, Organization organization, OrganizationMemberDTO organizationMemberDTO,
            boolean saveToElasticsearch) {
        OrganizationMember organizationMember = orgMemberRepo.getByUserAndOrganization(user, organization);
        if (organizationMember == null) {
            organizationMember = new OrganizationMember();
            organizationMember.setUser(user);
            organizationMember.setOrganization(organization);
        } else {
            if (!organizationMember.isFired()) {
                return organizationMember;
            }
            organizationMember.setFired(false);
            organizationMember.setOrganization(organization);
        }

        organizationMember.setEnabled(organizationMemberDTO.enabled);
        organizationMember.setManager(organizationMemberDTO.manager);

        if (organizationMember.getId() == null) {
            orgMemberRepo.persist(organizationMember);
        } else {
            organizationMember = orgMemberRepo.merge(organizationMember);
        }

        if (saveToElasticsearch) {
            try {
                elasticsearchService.addOrganization(user, organizationMember);
            } catch (Exception e) {
                LOG.error("Could not add organization for user " + user.getId() + " in elasticsearch", e);
            }
        }

        return organizationMember;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public OrganizationMember create(User currentUser, OrganizationMemberDTO organizationMemberDTO)
            throws ApplicationException {
        // security start
        securityService.authzCanModifyOrganizationMembers(currentUser, organizationMemberDTO.organization);
        // security finish

        User user = userRepo.get(organizationMemberDTO.userId);
        if (user == null) {
            throw new NotFoundException("User");
        }

        Organization organization = null;
        if (organizationMemberDTO.organization.id != null) {
            organization = organizationRepo.get(organizationMemberDTO.organization.id);
        } else if (organizationMemberDTO.organization.uiId != null) {
            organization = organizationRepo.getByUiId(organizationMemberDTO.organization.uiId.strip(), false);
        }
        if (organization == null) {
            throw new NotFoundException("Organization");
        }

        return create(user, organization, organizationMemberDTO, true);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public OrganizationMember update(User currentUser, OrganizationMemberDTO organizationMemberDTO)
            throws ApplicationException {
        // security start
        securityService.authzCanModifyOrganizationMember(currentUser, organizationMemberDTO.id);
        // security finish

        OrganizationMember organizationMember = orgMemberRepo
                .getNotFiredAndFetchUserAndOrganization(organizationMemberDTO.id);
        if (organizationMember == null) {
            throw new NotFoundException("Organization member");
        }

        organizationMember = checkRecordVersion(organizationMember, organizationMemberDTO.recordVersion);

        if (!organizationMemberDTO.manager || !organizationMemberDTO.enabled) {
            if (organizationMember.getUser().getId().equals(currentUser.getId())) {
                throw new BadRequestException("You can't demote yourself.");
            }
        }

        organizationMember.setEnabled(organizationMemberDTO.enabled);
        organizationMember.setManager(organizationMemberDTO.manager);

        organizationMember = orgMemberRepo.merge(organizationMember);

        User user = organizationMember.getUser();
        try {
            elasticsearchService.updateOrganizationMember(user, organizationMember);
        } catch (Exception e) {
            LOG.error("Could not update organization member " + organizationMember.getId() + " in elasticsearch", e);
        }

        return organizationMember;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public OrganizationMemberReadDTO get(User currentUser, Long id) throws ApplicationException {
        // security start
        Organization organization = securityService.authzCanReadOrganizationMember(currentUser, id);
        // security finish

        OrganizationMember organizationMember = orgMemberRepo.getNotFiredAndFetchUser(id);
        if (organizationMember == null) {
            throw new NotFoundException("Organization member not found.");
        }
        Long ownerUserId = (organization == null ? null : organization.getOwnerId());
        return OrganizationMemberReadDTO.fromEntity(organizationMember, ownerUserId);
    }

    private void delete(OrganizationMember organizationMember) throws ApplicationException {
        if (organizationMember == null) {
            throw new NotFoundException("Organization member not found.");
        }
        if (organizationMember.getUser().getId().equals(organizationMember.getOrganization().getOwner().getId())) {
            throw new BadRequestException("Owner of organization can't be deleted.");
        }

        projectMemberService.deleteBatchByOrganizationMember(organizationMember);

        organizationMember.setFired(true);
        organizationMember.setEnabled(false);
        organizationMember.setManager(false);
        orgMemberRepo.merge(organizationMember);

        User user = organizationMember.getUser();
        Organization organization = organizationMember.getOrganization();
        if (organization.isPrivate()) {
            taskSubscriberRepo.deleteByUserIdAndOrganizationId(user.getId(), organization.getId());
        } else {
            taskSubscriberRepo.deletePrivateByUserIdAndOrganizationId(user.getId(), organization.getId());
        }

        try {
            elasticsearchService.removeOrganization(user, organizationMember);
        } catch (Exception e) {
            LOG.error("Could not remove organization for user " + user.getId() + " in elasticsearch", e);
        }
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void delete(User currentUser, Long organizationMemberId) throws ApplicationException {
        // security start
        securityService.authzCanModifyOrganizationMember(currentUser, organizationMemberId);
        // security finish

        OrganizationMember organizationMember = orgMemberRepo.getNotFiredAndFetchUser(organizationMemberId);
        delete(organizationMember);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void leave(User currentUser, IdOrUiIdDTO organizationDTO) throws ApplicationException {
        // security start
        authnSecurityService.isAuthenticated(currentUser);
        // security finish

        OrganizationMember member = null;
        if (organizationDTO.id != null) {
            member = orgMemberRepo.getNotFiredByUserAndOrganizationId(
                    currentUser, organizationDTO.id);
        } else if (organizationDTO.uiId != null) {
            member = orgMemberRepo.getNotFiredByUserAndOrganizationUiId(
                    currentUser, organizationDTO.uiId);
        }
        member.setUser(currentUser);
        delete(member);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<OrganizationMemberUserDTO> listByMemberIds(User currentUser, Long organizationId,
            List<Long> memberIds) {
        // security start
        securityService.authzCanReadOrganizationMembers(currentUser, organizationId);
        // security finish

        List<OrganizationMember> memberList = jdbcOrgMemberRepo.listNotFiredByOrganizationIdAndMemberIdsAndFetchUser(
                organizationId, memberIds);
        return OrganizationMemberUserDTO.fromEntities(memberList);
    }

    private List<OrganizationMemberReadDTO> listByOrganization(Organization organization) {
        if (organization == null) {
            return new ArrayList<OrganizationMemberReadDTO>();
        }

        List<OrganizationMember> orgMemberList = orgMemberRepo
                .listNotFiredByOrganizationIdAndFetchUser(organization.getId());
        return OrganizationMemberReadDTO.fromEntities(orgMemberList, organization.getOwnerId());
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<OrganizationMemberReadDTO> listByOrganizationId(User currentUser, Long organizationId) {
        // security start
        Organization organization = securityService.authzCanReadOrganizationMembers(currentUser, organizationId);
        // security finish

        return listByOrganization(organization);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<OrganizationMemberReadDTO> listByOrganizationUiId(User currentUser, String organizationUiId) {
        // security start
        Organization organization = securityService.authzCanReadOrganizationMembers(currentUser, organizationUiId);
        // security finish

        return listByOrganization(organization);
    }

    @Override
    public List<OrganizationMemberUserDTO> search(
            User currentUser, String prefix, IdOrUiIdDTO organizationDTO, boolean meFirst) throws Exception {
        // security start
        securityService.authzCanReadOrganizationMembers(currentUser, organizationDTO);
        // security finish

        Long organizationId = organizationDTO.id;
        if (organizationId == null) {
            organizationId = organizationRepo.getOrganizationIdByUiId(organizationDTO.uiId);
        }
        if (organizationId == null) {
            return new ArrayList<OrganizationMemberUserDTO>();
        }

        SearchHits hits = elasticsearchService.searchByNameAndOrganization(prefix, organizationId);
        return OrganizationMemberUserDTO.fromSearchHits(hits, organizationId,
                ((meFirst && currentUser != null) ? currentUser.getId() : null));
    }
}
