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
package org.wrkr.clb.services.user;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.user.PublicProfileDTO;
import org.wrkr.clb.services.dto.user.PublicUserDTO;

@Validated
public interface UserService {

    public PublicProfileDTO get(@NotNull(message = "id must not be null") Long id) throws Exception;

    public List<PublicUserDTO> listByIds(@NotNull(message = "ids must not be null") List<Long> ids) throws Exception;

    public List<PublicUserDTO> searchForProject(User currentUser,
            @NotNull(message = "searchValue must not be null") String searchValue,
            @Valid IdOrUiIdDTO excludeProjectDTO) throws Exception;

    public List<PublicUserDTO> search(
            @NotNull(message = "prefix must not be null") String prefix) throws Exception;

    @Deprecated
    public TokenAndIvDTO getDialogChatToken(User currentUser, long userId) throws Exception;
}
