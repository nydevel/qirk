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
package org.wrkr.clb.services.user.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.model.user.UserFavorite;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.repo.user.JDBCUserFavoriteRepo;
import org.wrkr.clb.repo.user.UserFavoriteRepo;
import org.wrkr.clb.services.dto.MoveDTO;
import org.wrkr.clb.services.dto.user.UserFavoriteDTO;
import org.wrkr.clb.services.dto.user.UserFavoriteReadDTO;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.security.SecurityService;
import org.wrkr.clb.services.user.UserFavoriteService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.NotFoundException;
import org.wrkr.clb.services.util.http.JsonStatusCode;


@Validated
@Service
public class DefaultUserFavoriteService implements UserFavoriteService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultUserFavoriteService.class);

    @Autowired
    private UserFavoriteRepo favoriteRepo;

    @Autowired
    private JDBCUserFavoriteRepo jdbcFavoriteRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ProjectSecurityService projectSecurityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public UserFavoriteReadDTO create(User currentUser, UserFavoriteDTO userFavoriteDTO) throws ApplicationException {
        // security start
        projectSecurityService.authzCanAddProjectToFavorite(currentUser, userFavoriteDTO.projectId);
        // security finish
        UserFavorite userFavorite = new UserFavorite();

        userFavorite.setUser(currentUser);

        Project project = projectRepo.getAndFetchOrganization(userFavoriteDTO.projectId);
        if (project == null) {
            throw new NotFoundException("Project");
        }
        userFavorite.setProject(project);

        UserFavorite sameUserFavorite = favoriteRepo.getByUserAndProject(currentUser, project);
        if (sameUserFavorite != null) {
            throw new BadRequestException(JsonStatusCode.ALREADY_EXISTS, "This favorite already exists.");
        }

        UserFavorite firstUserFavorite = favoriteRepo.getFirstByUser(currentUser);

        favoriteRepo.persist(userFavorite);

        if (firstUserFavorite != null) {
            firstUserFavorite.setPrevious(userFavorite);
            favoriteRepo.merge(firstUserFavorite);
        }

        return UserFavoriteReadDTO.fromEntity(userFavorite);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public UserFavoriteReadDTO move(User currentUser, MoveDTO moveDTO) throws ApplicationException {
        // security start
        securityService.isAuthenticated(currentUser);
        // security finish

        UserFavorite toMove = favoriteRepo.getByIdAndUserAndFetchEverything(moveDTO.id, currentUser);
        if (toMove == null) {
            throw new NotFoundException("User favorite");
        }

        UserFavorite oldNext = toMove.getNext();
        if (oldNext != null) {
            oldNext.setPrevious(toMove.getPrevious());
            favoriteRepo.merge(oldNext);
        }

        if (moveDTO.previousId == null) {
            UserFavorite next = favoriteRepo.getFirstByUser(currentUser);
            if (next != null) {
                next.setPrevious(toMove);
                favoriteRepo.merge(next);
            }

            toMove.setPrevious(null);
            toMove = favoriteRepo.merge(toMove);
        } else {
            UserFavorite previous = favoriteRepo.getByIdAndUserAndFetchNext(moveDTO.previousId, currentUser);
            if (previous == null) {
                throw new NotFoundException("Previous user favorite");
            }

            UserFavorite next = previous.getNext();
            if (next != null) {
                next.setPrevious(toMove);
                favoriteRepo.merge(next);
            }

            toMove.setPrevious(previous);
            toMove.setNext(next);
            toMove = favoriteRepo.merge(toMove);
        }

        return UserFavoriteReadDTO.fromEntity(toMove);
    }

    private UserFavoriteReadDTO getDTO(User currentUser, UserFavorite favorite) {
        UserFavoriteReadDTO dto = UserFavoriteReadDTO.fromEntity(favorite);
        dto.canCreateTask = projectSecurityService.authzCanCreateTaskNoException(currentUser, favorite.getProject());
        return dto;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<UserFavoriteReadDTO> listByUser(User currentUser) {
        // security start
        securityService.isAuthenticated(currentUser);
        // security finish

        List<UserFavorite> unsortedList = jdbcFavoriteRepo
                .listByUserIdAndFetchProjectAndOrganizationAndMembershipForSecurity(currentUser.getId());

        UserFavorite lastFavorite = null;
        Map<Long, UserFavorite> favoriteIdToNextFavorite = new HashMap<Long, UserFavorite>(unsortedList.size());
        List<UserFavoriteReadDTO> dtoList = new ArrayList<UserFavoriteReadDTO>(unsortedList.size());
        for (UserFavorite favorite : unsortedList) {
            if (favorite.getPreviousId() == null) {
                lastFavorite = favorite;
                dtoList.add(getDTO(currentUser, lastFavorite));
            } else {
                favoriteIdToNextFavorite.put(favorite.getPreviousId(), favorite);
            }
        }

        while (lastFavorite != null) {
            // remove is a safety measure against recursion if data is corrupted
            lastFavorite = favoriteIdToNextFavorite.remove(lastFavorite.getId());
            if (lastFavorite != null) {
                dtoList.add(getDTO(currentUser, lastFavorite));
            }
        }

        return dtoList;
    }

    public void delete(UserFavorite userFavorite) {
        UserFavorite nextUserFavorite = userFavorite.getNext();
        UserFavorite previousUserFavorite = userFavorite.getPrevious();

        favoriteRepo.delete(userFavorite);
        if (nextUserFavorite != null) {
            nextUserFavorite.setPrevious(previousUserFavorite);
            favoriteRepo.merge(nextUserFavorite);
        }
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void deleteById(User currentUser, Long id) throws ApplicationException {
        // security start
        securityService.isAuthenticated(currentUser);
        // security finish

        UserFavorite userFavorite = favoriteRepo.getByIdAndUserAndFetchPreviousAndNext(id, currentUser);
        if (userFavorite == null) {
            throw new NotFoundException("User favorite");
        }

        delete(userFavorite);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    public void deleteByUserAndProjectId(User user, Long projectId) {
        if (user == null) {
            return;
        }

        UserFavorite userFavorite = favoriteRepo.getByUserAndProjectIdAndFetchPreviousAndNext(user, projectId);
        if (userFavorite != null) {
            delete(userFavorite);
        }
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    public void deleteByUserAndProjectUiId(User user, String projectUiId) {
        if (user == null) {
            return;
        }

        UserFavorite userFavorite = favoriteRepo.getByUserAndProjectUiIdAndFetchPreviousAndNext(user, projectUiId);
        if (userFavorite != null) {
            delete(userFavorite);
        }
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, propagation = Propagation.MANDATORY)
    public void deleteByUserIdAndProject(Long userId, Project project) {
        if (userId == null || project == null) {
            if (userId == null) {
                LOG.warn("userId in DefaultUserFavoriteService is null");
            }
            if (project == null) {
                LOG.warn("project in DefaultUserFavoriteService is null");
            }
            return;
        }

        UserFavorite userFavorite = favoriteRepo.getByUserIdAndProjectAndFetchPreviousAndNext(userId, project);
        if (userFavorite != null) {
            delete(userFavorite);
        }
    }
}
