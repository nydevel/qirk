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
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.NameAndUiIdDTO;
import org.wrkr.clb.services.dto.organization.OrganizationDTO;
import org.wrkr.clb.services.dto.organization.OrganizationReadDTO;
import org.wrkr.clb.services.organization.OrganizationService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;

@RestController
@RequestMapping(path = "organization", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class OrganizationController extends BaseExceptionHandlerController {

    @Autowired
    private OrganizationService organizationService;

    @GetMapping(value = "check-ui-id")
    public JsonContainer<ExistsDTO, Void> checkUiId(HttpSession session,
            @RequestParam(name = "ui_id") String uiId) throws Exception {
        long startTime = System.currentTimeMillis();
        ExistsDTO result = organizationService.checkUiId(getSessionUser(session), uiId.strip());
        logProcessingTimeFromStartTime(startTime, "checkUiId", uiId);
        return new JsonContainer<ExistsDTO, Void>(result);
    }

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<OrganizationReadDTO, Void> create(HttpSession session,
            @RequestBody OrganizationDTO organizationDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        Organization organization = organizationService.create(getSessionUser(session), organizationDTO);
        logProcessingTimeFromStartTime(startTime, "create", organizationDTO.uiId);
        return new JsonContainer<OrganizationReadDTO, Void>(OrganizationReadDTO.fromEntityWithLanguages(organization));
    }

    @PutMapping(value = "/")
    public JsonContainer<OrganizationReadDTO, Void> update(HttpSession session,
            @RequestBody OrganizationDTO organizationDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        OrganizationReadDTO organizationReadDTO = organizationService.update(getSessionUser(session), organizationDTO);
        logProcessingTimeFromStartTime(startTime, "update", organizationDTO.id);
        return new JsonContainer<OrganizationReadDTO, Void>(organizationReadDTO);
    }

    /*@formatter:off
    @PostMapping(value = "dropbox")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<OrganizationReadDTO, Void> addDropbox(HttpSession session,
            @RequestBody OAuthCodeDTO codeDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        OrganizationReadDTO organizationDTO = organizationService.addDropbox(getSessionUser(session), codeDTO);
        logProcessingTimeFromStartTime(startTime, "addDropbox", codeDTO.id);
        return new JsonContainer<OrganizationReadDTO, Void>(organizationDTO);
    }

    @DeleteMapping(value = "dropbox")
    public JsonContainer<OrganizationReadDTO, Void> removeDropbox(HttpSession session,
            @RequestBody RecordVersionDTO dto) throws Exception {
        long startTime = System.currentTimeMillis();
        OrganizationReadDTO organizationDTO = organizationService.removeDropbox(getSessionUser(session), dto);
        logProcessingTimeFromStartTime(startTime, "removeDropbox", dto.id);
        return new JsonContainer<OrganizationReadDTO, Void>(organizationDTO);
    }
    @formatter:on*/

    @GetMapping(value = "/")
    public JsonContainer<OrganizationReadDTO, Void> read(HttpSession session,
            @RequestParam(name = "id", required = false) Long id,
            @RequestParam(name = "ui_id", required = false) String uiId,
            @RequestParam(name = "include_can_leave", required = false, defaultValue = "false") Boolean includeCanLeave)
            throws Exception {
        long startTime = System.currentTimeMillis();
        OrganizationReadDTO organizationDTO = null;
        if (id != null) {
            organizationDTO = organizationService.get(getSessionUser(session), id, includeCanLeave);
        } else if (uiId != null) {
            organizationDTO = organizationService.getByUiId(getSessionUser(session), uiId.strip(), includeCanLeave);
        } else {
            throw new BadRequestException("Neither parameter 'id' nor parameter 'ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "read", id, uiId, includeCanLeave);
        return new JsonContainer<OrganizationReadDTO, Void>(organizationDTO);
    }

    @GetMapping(value = "list")
    public JsonContainer<NameAndUiIdDTO, Void> listByUser(HttpSession session,
            @RequestParam(name = "can_create_project", required = false, defaultValue = "false") Boolean canCreateProject) {
        long startTime = System.currentTimeMillis();
        List<NameAndUiIdDTO> organizationDTOList = organizationService.listByUser(getSessionUser(session), canCreateProject);
        logProcessingTimeFromStartTime(startTime, "listByUser");
        return new JsonContainer<NameAndUiIdDTO, Void>(organizationDTOList);
    }

    /*@formatter:off
    @GetMapping(value = "search")
    public JsonContainer<NameAndUiIdDTO, Void> search(@SuppressWarnings("unused") HttpSession session,
            @RequestParam(name = "prefix") String prefix) throws Exception {
        long startTime = System.currentTimeMillis();
        List<NameAndUiIdDTO> dtoList = organizationService.search(prefix.strip());
        logProcessingTimeFromStartTime(startTime, "search", prefix);
        return new JsonContainer<NameAndUiIdDTO, Void>(dtoList);
    }
    @formatter:on*/
}
