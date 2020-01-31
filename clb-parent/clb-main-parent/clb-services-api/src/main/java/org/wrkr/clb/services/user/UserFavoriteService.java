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
package org.wrkr.clb.services.user;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.MoveDTO;
import org.wrkr.clb.services.dto.user.UserFavoriteDTO;
import org.wrkr.clb.services.dto.user.UserFavoriteReadDTO;


@Validated
public interface UserFavoriteService {

    UserFavoriteReadDTO create(User currentUser,
            @Valid UserFavoriteDTO userFavoriteDTO) throws Exception;

    @Validated(OnUpdate.class)
    UserFavoriteReadDTO move(User currentUser,
            @Valid MoveDTO moveDTO) throws Exception;

    List<UserFavoriteReadDTO> listByUser(User currentUser);

    void deleteById(User currentUser,
            @NotNull(message = "id in UserFavoriteService not be null") Long id) throws Exception;

    void deleteByUserAndProjectId(User user, @NotNull(message = "projectId in ProjectService must not be null") Long projectId);

    void deleteByUserAndProjectUiId(User user,
            @NotNull(message = "projectUiId in ProjectService must not be null") String projectUiId);

    void deleteByUserIdAndProject(Long userId, Project project);
}
