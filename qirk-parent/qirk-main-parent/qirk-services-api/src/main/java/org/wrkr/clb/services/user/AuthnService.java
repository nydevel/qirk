/**
 * Copyright Shifu.group 2019
 */
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.LoginDTO;
import org.wrkr.clb.services.util.exception.BadRequestException;


@Validated
public interface AuthnService {

    public HttpServletResponse login(HttpServletResponse response, HttpSession session,
            User user, String forwardedFor);

    public HttpServletResponse login(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            @Valid LoginDTO loginDTO, String forwardedFor) throws AuthenticationException, BadRequestException;
}
