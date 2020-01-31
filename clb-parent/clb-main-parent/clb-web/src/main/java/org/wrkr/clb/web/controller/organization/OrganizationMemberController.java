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
package org.wrkr.clb.web.controller.organization;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberReadDTO;
import org.wrkr.clb.services.dto.organization.OrganizationMemberUserDTO;
import org.wrkr.clb.services.organization.OrganizationMemberService;
import org.wrkr.clb.services.project.ProjectMemberService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "organization-member", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OrganizationMemberController extends BaseExceptionHandlerController {

    @Autowired
    private OrganizationMemberService organizationMemberService;

    @Autowired
    private ProjectMemberService projectMemberService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<OrganizationMemberReadDTO, Void> create(HttpSession session,
            @RequestBody OrganizationMemberDTO organizationMemberDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        OrganizationMember organizationMember = organizationMemberService.create(getSessionUser(session),
                organizationMemberDTO);
        logProcessingTimeFromStartTime(startTime, "create", organizationMemberDTO.organization, organizationMemberDTO.userId);
        return new JsonContainer<OrganizationMemberReadDTO, Void>(OrganizationMemberReadDTO.fromEntity(organizationMember));
    }

    @PutMapping(value = "/")
    public JsonContainer<OrganizationMemberReadDTO, Void> update(HttpSession session,
            @RequestBody OrganizationMemberDTO organizationMemberDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        OrganizationMember organizationMember = organizationMemberService.update(getSessionUser(session),
                organizationMemberDTO);
        logProcessingTimeFromStartTime(startTime, "update", organizationMemberDTO.id);
        return new JsonContainer<OrganizationMemberReadDTO, Void>(OrganizationMemberReadDTO.fromEntity(organizationMember));
    }

    @GetMapping(value = "/")
    public JsonContainer<OrganizationMemberReadDTO, Void> read(HttpSession session,
            @RequestParam(name = "id") Long id) throws Exception {
        long startTime = System.currentTimeMillis();
        OrganizationMemberReadDTO organizationMemberDTO = organizationMemberService.get(getSessionUser(session), id);
        logProcessingTimeFromStartTime(startTime, "read", id);
        return new JsonContainer<OrganizationMemberReadDTO, Void>(organizationMemberDTO);
    }

    @DeleteMapping(value = "/")
    public JsonContainer<Void, Void> delete(HttpSession session, @RequestBody IdDTO idDTO)
            throws Exception {
        long startTime = System.currentTimeMillis();
        organizationMemberService.delete(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "delete", idDTO.id);
        return new JsonContainer<Void, Void>();
    }

    @DeleteMapping(value = "leave")
    public JsonContainer<Void, Void> leave(HttpSession session,
            @RequestBody OrganizationMemberDTO organizationDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        User user = getSessionUser(session);
        organizationMemberService.leave(user, organizationDTO.organization);
        logProcessingTimeFromStartTime(startTime, "leave",
                user, organizationDTO.organization.id, organizationDTO.organization.uiId);
        return new JsonContainer<Void, Void>();
    }

    @Deprecated
    @GetMapping(value = "list")
    public JsonContainer<OrganizationMemberReadDTO, Void> list(HttpSession session,
            @RequestParam(name = "organization_id", required = false) Long organizationId,
            @RequestParam(name = "organization_ui_id", required = false) String organizationUiId) throws BadRequestException {
        return listByOrganzation(session, organizationId, organizationUiId);
    }

    @GetMapping(value = "list-by-organization")
    public JsonContainer<OrganizationMemberReadDTO, Void> listByOrganzation(HttpSession session,
            @RequestParam(name = "organization_id", required = false) Long organizationId,
            @RequestParam(name = "organization_ui_id", required = false) String organizationUiId) throws BadRequestException {
        long startTime = System.currentTimeMillis();
        List<OrganizationMemberReadDTO> organizationMemberDTOList = new ArrayList<OrganizationMemberReadDTO>();
        if (organizationId != null) {
            organizationMemberDTOList = organizationMemberService.listByOrganizationId(
                    getSessionUser(session), organizationId);
        } else if (organizationUiId != null) {
            organizationMemberDTOList = organizationMemberService.listByOrganizationUiId(
                    getSessionUser(session), organizationUiId.strip());
        } else {
            throw new BadRequestException("Neither parameter 'organization_id' nor parameter 'organization_ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "listByOrganzation", organizationId, organizationUiId);
        return new JsonContainer<OrganizationMemberReadDTO, Void>(organizationMemberDTOList);
    }

    @GetMapping(value = "list-by-ids")
    public JsonContainer<OrganizationMemberUserDTO, Void> listByIds(HttpSession session,
            @RequestParam(name = "organization_id", required = false) Long organizationId,
            @RequestParam(name = "ids", required = false, defaultValue = "false") List<Long> ids) {
        long startTime = System.currentTimeMillis();
        List<OrganizationMemberUserDTO> organizationMemberDTOList = organizationMemberService.listByMemberIds(
                getSessionUser(session), organizationId, ids);
        logProcessingTimeFromStartTime(startTime, "listByIds", ids);
        return new JsonContainer<OrganizationMemberUserDTO, Void>(organizationMemberDTOList);
    }

    @GetMapping(value = "search")
    public JsonContainer<OrganizationMemberUserDTO, Void> search(HttpSession session,
            @RequestParam(name = "prefix") String prefix,
            @RequestParam(name = "organization_id", required = false) Long organizationId,
            @RequestParam(name = "organization_ui_id", required = false) String organizationUiId,
            @RequestParam(name = "me_first", required = false, defaultValue = "false") Boolean meFirst) throws Exception {
        long startTime = System.currentTimeMillis();
        List<OrganizationMemberUserDTO> organizationMemberDTOList = organizationMemberService
                .search(getSessionUser(session), prefix.strip(), new IdOrUiIdDTO(organizationId, organizationUiId), meFirst);
        logProcessingTimeFromStartTime(startTime, "search", organizationId, organizationUiId, meFirst);
        return new JsonContainer<OrganizationMemberUserDTO, Void>(organizationMemberDTOList);
    }

    @GetMapping(value = "search-for-project")
    public JsonContainer<OrganizationMemberUserDTO, Void> searchForProject(HttpSession session,
            @RequestParam(name = "prefix") String prefix,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId,
            @RequestParam(name = "me_first", required = false, defaultValue = "false") Boolean meFirst) throws Exception {
        long startTime = System.currentTimeMillis();
        List<OrganizationMemberUserDTO> organizationMemberDTOList = projectMemberService
                .search(getSessionUser(session), prefix.strip(), new IdOrUiIdDTO(projectId, projectUiId), meFirst);
        logProcessingTimeFromStartTime(startTime, "searchForProject", projectId, projectUiId, meFirst);
        return new JsonContainer<OrganizationMemberUserDTO, Void>(organizationMemberDTOList);
    }
}
