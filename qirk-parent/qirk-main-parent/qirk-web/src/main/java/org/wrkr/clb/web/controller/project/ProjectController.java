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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.services.dto.ChatPermissionsDTO;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.project.ProjectDTO;
import org.wrkr.clb.services.dto.project.ProjectDocDTO;
import org.wrkr.clb.services.dto.project.ProjectInviteOptionDTO;
import org.wrkr.clb.services.dto.project.ProjectReadDTO;
import org.wrkr.clb.services.dto.project.ProjectWithOrganizationDTO;
import org.wrkr.clb.services.project.ProjectService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "project", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class ProjectController extends BaseExceptionHandlerController {

    @Autowired
    private ProjectService projectService;

    @GetMapping(value = "check-ui-id")
    public JsonContainer<ExistsDTO, Void> checkUiId(HttpSession session,
            @RequestParam(name = "ui_id") String uiId) throws Exception {
        long startTime = System.currentTimeMillis();
        ExistsDTO result = projectService.checkUiId(getSessionUser(session), uiId.strip());
        logProcessingTimeFromStartTime(startTime, "checkUiId", uiId);
        return new JsonContainer<ExistsDTO, Void>(result);
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<ProjectReadDTO, Void> create(HttpSession session,
            @RequestBody ProjectDTO projectDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectReadDTO projectReadDTO = projectService.create(getSessionUser(session), projectDTO);
        logProcessingTimeFromStartTime(startTime, "create", projectDTO.uiId, projectDTO.makeMeMember);
        return new JsonContainer<ProjectReadDTO, Void>(projectReadDTO);
    }

    @PutMapping(value = "/")
    public JsonContainer<ProjectReadDTO, Void> update(HttpSession session,
            @RequestBody ProjectDTO projectDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectReadDTO projectReadDTO = projectService.update(getSessionUser(session), projectDTO);
        logProcessingTimeFromStartTime(startTime, "update", projectDTO.id);
        return new JsonContainer<ProjectReadDTO, Void>(projectReadDTO);
    }

    /*@formatter:off
    @PutMapping(value = "public")
    public JsonContainer<Void, Void> makePublic(HttpSession session,
            @RequestBody RecordVersionDTO projectDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        projectService.makePublic(getSessionUser(session), projectDTO);
        logProcessingTimeFromStartTime(startTime, "makePublic", projectDTO.id);
        return new JsonContainer<Void, Void>();
    }
    @formatter:on*/

    @GetMapping(value = "documentation")
    public JsonContainer<ProjectDocDTO, Void> readDocumentation(HttpSession session,
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "ui_id", required = false) String uiId) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectDocDTO projectDTO = null;
        if (id != null) {
            projectDTO = projectService.getDocumentation(getSessionUser(session), id);
        } else if (uiId != null) {
            projectDTO = projectService.getDocumentationByUiId(getSessionUser(session), uiId.strip());
        } else {
            throw new BadRequestException("Neither parameter 'id' nor parameter 'ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "readDocumentation", id, uiId);
        return new JsonContainer<ProjectDocDTO, Void>(projectDTO);
    }

    @PutMapping(value = "documentation")
    public JsonContainer<ProjectReadDTO, Void> updateDocumentation(HttpSession session,
            @RequestBody ProjectDocDTO documentationDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectReadDTO projectDTO = projectService.updateDocumentation(getSessionUser(session), documentationDTO);
        logProcessingTimeFromStartTime(startTime, "updateDocumentation", documentationDTO.id);
        return new JsonContainer<ProjectReadDTO, Void>(projectDTO);
    }

    /*@formatter:off
    @PostMapping(value = "dropbox")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<ProjectReadDTO, Void> addDropbox(HttpSession session,
            @RequestBody OAuthCodeDTO codeDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectReadDTO projectReadDTO = projectService.addDropbox(getSessionUser(session), codeDTO);
        logProcessingTimeFromStartTime(startTime, "addDropbox", codeDTO.id);
        return new JsonContainer<ProjectReadDTO, Void>(projectReadDTO);
    }

    @GetMapping(value = "dropbox")
    public JsonContainer<ProjectDropboxDTO, Void> readDropbox(HttpSession session,
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "ui_id", required = false) String uiId) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectDropboxDTO dropboxDTO = null;
        if (id != null) {
            dropboxDTO = projectService.getDropbox(getSessionUser(session), id);
        } else if (uiId != null) {
            dropboxDTO = projectService.getDropboxByUiId(getSessionUser(session), uiId.strip());
        } else {
            throw new BadRequestException("Neither parameter 'id' nor parameter 'ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "readDropbox", id, uiId);
        return new JsonContainer<ProjectDropboxDTO, Void>(dropboxDTO);
    }

    @DeleteMapping(value = "dropbox")
    public JsonContainer<ProjectReadDTO, Void> removeDropbox(HttpSession session,
            @RequestBody RecordVersionDTO dto) throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectReadDTO projectDTO = projectService.removeDropbox(getSessionUser(session), dto);
        logProcessingTimeFromStartTime(startTime, "removeDropbox", dto.id);
        return new JsonContainer<ProjectReadDTO, Void>(projectDTO);
    }
    @formatter:on*/

    @GetMapping(value = "/")
    public JsonContainer<ProjectReadDTO, Void> read(HttpSession session,
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "ui_id", required = false) String uiId,
            @RequestParam(name = "include_application", required = false, defaultValue = "false") boolean includeApplication)
            throws Exception {
        long startTime = System.currentTimeMillis();
        ProjectReadDTO projectDTO = null;
        if (id != null) {
            projectDTO = projectService.get(getSessionUser(session), id, includeApplication);
        } else if (uiId != null) {
            projectDTO = projectService.getByUiId(getSessionUser(session), uiId.strip(), includeApplication);
        } else {
            throw new BadRequestException("Neither parameter 'id' nor parameter 'ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "read", id, uiId, includeApplication);
        return new JsonContainer<ProjectReadDTO, Void>(projectDTO);
    }

    @GetMapping(value = "list-by-user")
    public JsonContainer<ProjectWithOrganizationDTO, Void> listByUser(HttpSession session,
            @RequestParam(name = "managed", required = false, defaultValue = "false") boolean managed) {
        long startTime = System.currentTimeMillis();
        List<ProjectWithOrganizationDTO> projectDTOList = new ArrayList<ProjectWithOrganizationDTO>();
        if (managed) {
            projectDTOList = projectService.listManagedByUser(getSessionUser(session));
        } else {
            projectDTOList = projectService.listByUser(getSessionUser(session));
        }
        logProcessingTimeFromStartTime(startTime, "listByUser", managed);
        return new JsonContainer<ProjectWithOrganizationDTO, Void>(projectDTOList);
    }

    @Deprecated
    @GetMapping(value = "list-invite-options")
    public JsonContainer<ProjectInviteOptionDTO, Void> listInviteOptions(HttpSession session,
            @RequestParam(name = "user_id") long userId) {
        long startTime = System.currentTimeMillis();
        List<ProjectInviteOptionDTO> projectDTOList = projectService.listInviteOptions(getSessionUser(session), userId);
        logProcessingTimeFromStartTime(startTime, "listInviteOptions", userId);
        return new JsonContainer<ProjectInviteOptionDTO, Void>(projectDTOList);
    }

    /*@formatter:off
    @GetMapping(value = "top")
    public JsonContainer<ProjectReadDTO, Void> listTop(@SuppressWarnings("unused") HttpSession session)
            throws Exception {
        long startTime = System.currentTimeMillis();
        List<ProjectReadDTO> projectDTOList = projectService.listTop();
        logProcessingTimeFromStartTime(startTime, "listTop");
        return new JsonContainer<ProjectReadDTO, Void>(projectDTOList);
    }

    @GetMapping(value = "search")
    public JsonContainer<ProjectWithOrganizationDTO, Void> search(@SuppressWarnings("unused") HttpSession session,
            @RequestParam(name = "text") String text,
            @RequestParam(name = "include_description", required = false, defaultValue = "false") boolean includeDescription)
            throws Exception {
        long startTime = System.currentTimeMillis();
        List<ProjectWithOrganizationDTO> projectDTOList = projectService.search(text.strip(), includeDescription);
        logProcessingTimeFromStartTime(startTime, "search", text, includeDescription);
        return new JsonContainer<ProjectWithOrganizationDTO, Void>(projectDTOList);
    }
    @formatter:on*/

    @GetMapping(value = "chat-token")
    public JsonContainer<ChatPermissionsDTO, Void> getChatToken(HttpSession session,
            @RequestParam(name = "id") Long id) throws Exception {
        long startTime = System.currentTimeMillis();
        ChatPermissionsDTO tokenDTO = projectService.getChatToken(getSessionUser(session), id);
        logProcessingTimeFromStartTime(startTime, "getChatToken", id);
        return new JsonContainer<ChatPermissionsDTO, Void>(tokenDTO);
    }
}
