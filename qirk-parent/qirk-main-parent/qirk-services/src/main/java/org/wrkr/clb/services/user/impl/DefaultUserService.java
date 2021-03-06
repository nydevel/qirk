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
import org.wrkr.clb.repo.project.JDBCProjectRepo;
import org.wrkr.clb.repo.user.JDBCUserRepo;
import org.wrkr.clb.repo.user.UserRepo;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchUserService;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.user.PublicUserDTO;
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
    private JDBCProjectRepo projectRepo;

    @Autowired
    private ElasticsearchUserService elasticsearchService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ProjectSecurityService projectSecurityService;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public PublicUserDTO get(Long id) throws Exception {
        User user = jdbcUserRepo.getByIdForPublic(id);
        if (user == null) {
            throw new NotFoundException("User");
        }
        return PublicUserDTO.fromEntity(user);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<PublicUserDTO> listByIds(List<Long> ids) throws Exception {
        List<User> userList = userRepo.listByIds(ids);
        return PublicUserDTO.fromEntities(userList);
    }

    @Override
    public List<PublicUserDTO> searchForProject(User currentUser, String searchValue,
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

        SearchHits hits = elasticsearchService.searchByNameAndExcludeProject(searchValue, excludeProjectId);
        return PublicUserDTO.fromSearchHits(hits);
    }

    @Override
    public List<PublicUserDTO> search(String prefix) throws Exception {
        SearchHits hits = elasticsearchService.searchByName(prefix);
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
