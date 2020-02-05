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
package org.wrkr.clb.web.controller.project;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.services.dto.TokenRejectDTO;
import org.wrkr.clb.services.dto.project.GrantedPermissionsProjectInviteReadDTO;
import org.wrkr.clb.services.dto.user.RegisteredDTO;
import org.wrkr.clb.services.dto.user.TokenRegisterDTO;
import org.wrkr.clb.services.project.GrantedPermissionsProjectInviteService;
import org.wrkr.clb.services.project.ProjectInviteTokenService;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "project-invite-token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ProjectInviteTokenController extends BaseExceptionHandlerController {

    @Autowired
    private ProjectInviteTokenService inviteTokenService;

    @Autowired
    private GrantedPermissionsProjectInviteService projectInviteService;

    @GetMapping(value = "check-token")
    public JsonContainer<RegisteredDTO, Void> checkEmail(@SuppressWarnings("unused") HttpSession session,
            @RequestParam(name = "token") String token) throws Exception {
        long startTime = System.currentTimeMillis();
        RegisteredDTO dto = inviteTokenService.checkToken(token);
        logProcessingTimeFromStartTime(startTime, "checkEmail");
        return new JsonContainer<RegisteredDTO, Void>(dto);
    }

    @PostMapping(value = "accept")
    public JsonContainer<Void, Void> accept(@SuppressWarnings("unused") HttpSession session,
            @RequestBody TokenRegisterDTO dto) throws Exception {
        long startTime = System.currentTimeMillis();
        projectInviteService.acceptByToken(dto);
        logProcessingTimeFromStartTime(startTime, "accept");
        return new JsonContainer<Void, Void>();
    }

    @PutMapping(value = "reject")
    public JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void> reject(@SuppressWarnings("unused") HttpSession session,
            @RequestBody TokenRejectDTO rejectDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        GrantedPermissionsProjectInviteReadDTO projectInviteDTO = projectInviteService.rejectByToken(rejectDTO);
        logProcessingTimeFromStartTime(startTime, "reject", rejectDTO.reported);
        return new JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void>(projectInviteDTO);
    }
}
