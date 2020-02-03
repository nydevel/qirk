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
package org.wrkr.clb.services.project;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.validation.groups.OnCreate;
import org.wrkr.clb.common.validation.groups.OnUpdate;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.project.IssueDTO;
import org.wrkr.clb.services.dto.project.IssueReadDTO;


@Validated
public interface IssueService {

    @Validated(OnCreate.class)
    public IssueReadDTO create(User currentUser,
            @Valid IssueDTO issueDTO) throws Exception;

    @Validated(OnUpdate.class)
    public IssueReadDTO update(User currentUser,
            @Valid IssueDTO issueDTO) throws Exception;

    public IssueReadDTO get(User currentUser,
            @NotNull(message = "id must not be null") Long id) throws Exception;

    public List<IssueReadDTO> listByProjectId(User currentUser,
            @NotNull(message = "projectId must not be null") Long projectId);

    public List<IssueReadDTO> listByProjectUiId(User currentUser,
            @NotNull(message = "projectUiId must not be null") String projectUiId);

    public ChatPermissionsDTO getChatToken(User currentUser,
            @NotNull(message = "issueId must not be null") Long issueId) throws Exception;

}
