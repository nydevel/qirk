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
package org.wrkr.clb.services.user.impl;

import java.util.List;

import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.TokenGenerator;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.common.crypto.token.chat.ChatTokenData;
import org.wrkr.clb.common.util.chat.ChatType;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.LanguageRepo;
import org.wrkr.clb.repo.TagRepo;
import org.wrkr.clb.repo.organization.OrganizationRepo;
import org.wrkr.clb.repo.project.JDBCProjectRepo;
import org.wrkr.clb.repo.user.JDBCUserRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.user.PublicProfileDTO;
import org.wrkr.clb.services.dto.user.PublicUserDTO;
import org.wrkr.clb.services.dto.user.PublicUserMembershipDTO;
import org.wrkr.clb.services.security.OrganizationSecurityService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.security.SecurityService;
import org.wrkr.clb.services.user.UserService;
import org.wrkr.clb.services.util.exception.NotFoundException;


//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultUserService implements UserService {

    @SuppressWarnings("unused")
    private final static Logger LOG = LoggerFactory.getLogger(DefaultUserService.class);

    // chat token config values
    private Integer chatTokenNotBeforeToleranceSeconds;
    private Integer chatTokenLifetimeSeconds;

    public Integer getChatTokenNotBeforeToleranceSeconds() {
        return chatTokenNotBeforeToleranceSeconds;
    }

    public void setChatTokenNotBeforeToleranceSeconds(Integer chatTokenNotBeforeToleranceSeconds) {
        this.chatTokenNotBeforeToleranceSeconds = chatTokenNotBeforeToleranceSeconds;
    }

    public Integer getChatTokenLifetimeSeconds() {
        return chatTokenLifetimeSeconds;
    }

    public void setChatTokenLifetimeSeconds(Integer chatTokenLifetimeSeconds) {
        this.chatTokenLifetimeSeconds = chatTokenLifetimeSeconds;
    }

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JDBCUserRepo jdbcUserRepo;

    @Autowired
    private OrganizationRepo organizationRepo;

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private LanguageRepo languageRepo;

    @Autowired
    private JDBCProjectRepo projectRepo;

    @Autowired
    private ElasticsearchUserService elasticsearchService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private OrganizationSecurityService orgSecurityService;

    @Autowired
    private ProjectSecurityService projectSecurityService;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public PublicProfileDTO get(Long id) throws Exception {
        User user = jdbcUserRepo.getByIdForPublicProfile(id);
        if (user == null) {
            throw new NotFoundException("User");
        }

        user.setTags(tagRepo.listByUserId(user.getId()));
        user.setLanguages(languageRepo.listByUserId(user.getId()));
        return PublicProfileDTO.fromEntity(user);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<PublicUserDTO> listByIds(List<Long> ids) throws Exception {
        List<User> userList = userRepo.listByIds(ids);
        return PublicUserDTO.fromEntities(userList);
    }

    @Override
    public List<PublicUserDTO> searchForOrganization(
            User currentUser, String searchValue, IdOrUiIdDTO excludeOrganizationDTO)
            throws Exception {
        // security start
        orgSecurityService.authzCanReadOrganization(currentUser, excludeOrganizationDTO);
        // security finish

        Long excludeOrganizationId = excludeOrganizationDTO.id;
        if (excludeOrganizationId == null) {
            excludeOrganizationId = organizationRepo.getOrganizationIdByUiId(excludeOrganizationDTO.uiId);
        }
        if (excludeOrganizationId == null) {
            throw new NotFoundException("Organization");
        }

        SearchHits hits = elasticsearchService.searchByNameAndExcludeOrganization(searchValue, excludeOrganizationId);

        return PublicUserDTO.fromSearchHits(hits);
    }

    @Override
    public List<PublicUserMembershipDTO> searchForProject(User currentUser, String searchValue,
            IdOrUiIdDTO excludeProjectDTO)
            throws Exception {
        // security start
        projectSecurityService.authzCanModifyProjectInvites(currentUser, excludeProjectDTO);
        // security finish

        Long excludeProjectId = excludeProjectDTO.id;
        if (excludeProjectId == null) {
            excludeProjectId = projectRepo.getProjectIdByUiId(excludeProjectDTO.uiId);
        }
        if (excludeProjectId == null) {
            throw new NotFoundException("Project");
        }

        Long organizationId = organizationRepo.getOrganizationIdByProjectId(excludeProjectId);
        SearchHits memberHits = elasticsearchService.searchByNameAndOrganizationAndExcludeProject(
                searchValue, organizationId, excludeProjectId);
        SearchHits nonMemberHits = elasticsearchService.searchByNameAndExcludeOrganizationAndProject(
                searchValue, organizationId, excludeProjectId);

        return PublicUserMembershipDTO.fromSearchHits(memberHits, nonMemberHits, organizationId);
    }

    @Override
    public List<PublicUserDTO> search(String prefix, boolean includeTags) throws Exception {
        SearchHits hits = null;
        if (includeTags) {
            hits = elasticsearchService.searchByNameOrTags(prefix);
        } else {
            hits = elasticsearchService.searchByName(prefix);
        }

        List<PublicUserDTO> dtoList = PublicUserDTO.fromSearchHits(hits);
        return dtoList;
    }

    @Deprecated
    @Override
    public TokenAndIvDTO getDialogChatToken(User currentUser, long userId) throws Exception {
        // security
        securityService.isAuthenticated(currentUser);
        // security

        ChatTokenData tokenData = new ChatTokenData();
        tokenData.chatType = ChatType.DIALOG;
        tokenData.chatId = userId;
        tokenData.senderId = currentUser.getId();
        tokenData.write = true;

        long now = System.currentTimeMillis();
        tokenData.notBefore = now - chatTokenNotBeforeToleranceSeconds * 1000;
        tokenData.notOnOrAfter = now + chatTokenLifetimeSeconds * 1000;

        return tokenGenerator.encrypt(tokenData.toJson());
    }
}
