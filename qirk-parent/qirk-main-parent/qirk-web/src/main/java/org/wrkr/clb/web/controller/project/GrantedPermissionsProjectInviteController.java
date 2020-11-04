package org.wrkr.clb.web.controller.project;

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
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.RejectDTO;
import org.wrkr.clb.services.dto.project.GrantedPermissionsProjectInviteDTO;
import org.wrkr.clb.services.dto.project.GrantedPermissionsProjectInviteReadDTO;
import org.wrkr.clb.services.project.GrantedPermissionsProjectInviteService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "granted-permissions-project-invite", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GrantedPermissionsProjectInviteController extends BaseExceptionHandlerController {

    @Autowired
    private GrantedPermissionsProjectInviteService projectInviteService;

    @PostMapping(value = "/")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void> create(HttpSession session,
            @RequestBody GrantedPermissionsProjectInviteDTO projectInviteDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        GrantedPermissionsProjectInviteReadDTO dto = projectInviteService.create(getSessionUser(session), projectInviteDTO);
        logProcessingTimeFromStartTime(startTime, "create");
        return new JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void>(dto);
    }

    @PostMapping(value = "create-by-email")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void> createByEmail(HttpSession session,
            @RequestBody GrantedPermissionsProjectInviteDTO projectInviteDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        GrantedPermissionsProjectInviteReadDTO dto = projectInviteService.createByEmail(getSessionUser(session),
                projectInviteDTO);
        logProcessingTimeFromStartTime(startTime, "createByEmail");
        return new JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void>(dto);
    }

    @PutMapping(value = "/")
    public JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void> update(HttpSession session,
            @RequestBody GrantedPermissionsProjectInviteDTO projectInviteDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        GrantedPermissionsProjectInviteReadDTO projectInviteReadDTO = projectInviteService.update(getSessionUser(session),
                projectInviteDTO);
        logProcessingTimeFromStartTime(startTime, "update");
        return new JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void>(projectInviteReadDTO);
    }

    @GetMapping(value = "/")
    public JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void> read(HttpSession session,
            @RequestParam(name = "id") Long id) throws Exception {
        long startTime = System.currentTimeMillis();
        GrantedPermissionsProjectInviteReadDTO projectInviteDTO = projectInviteService.get(getSessionUser(session), id);
        logProcessingTimeFromStartTime(startTime, "read", id);
        return new JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void>(projectInviteDTO);
    }

    @GetMapping(value = "list-by-user")
    public JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void> listByUser(HttpSession session) throws Exception {
        long startTime = System.currentTimeMillis();
        List<GrantedPermissionsProjectInviteReadDTO> projectInviteDTOList = projectInviteService
                .listByUser(getSessionUser(session));
        logProcessingTimeFromStartTime(startTime, "listByUser");
        return new JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void>(projectInviteDTOList);
    }

    @PostMapping(value = "accept")
    public JsonContainer<Void, Void> accept(HttpSession session,
            @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        projectInviteService.accept(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "accept", idDTO.id);
        return new JsonContainer<Void, Void>();
    }

    @PutMapping(value = "reject")
    public JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void> reject(HttpSession session,
            @RequestBody RejectDTO rejectDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        GrantedPermissionsProjectInviteReadDTO projectInviteDTO = projectInviteService.reject(getSessionUser(session), rejectDTO);
        logProcessingTimeFromStartTime(startTime, "reject", rejectDTO.id, rejectDTO.reported);
        return new JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void>(projectInviteDTO);
    }

    @GetMapping(value = "list-by-project")
    public JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void> listByProject(HttpSession session,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId,
            @RequestParam(name = "include_email", required = false, defaultValue = "false") Boolean includeEmail)
            throws Exception {
        long startTime = System.currentTimeMillis();
        List<GrantedPermissionsProjectInviteReadDTO> projectInviteDTOList = new ArrayList<GrantedPermissionsProjectInviteReadDTO>();
        if (projectId != null) {
            projectInviteDTOList = projectInviteService.listByProjectId(getSessionUser(session), projectId, includeEmail);
        } else if (projectUiId != null) {
            projectInviteDTOList = projectInviteService.listByProjectUiId(getSessionUser(session),
                    projectUiId.strip(), includeEmail);
        } else {
            throw new BadRequestException("Neither parameter 'project_id' nor parameter 'project_ui_id' is present.");
        }
        logProcessingTimeFromStartTime(startTime, "listByProject", projectId, projectUiId);
        return new JsonContainer<GrantedPermissionsProjectInviteReadDTO, Void>(projectInviteDTOList);
    }

    @DeleteMapping(value = "cancel")
    public JsonContainer<Void, Void> cancel(HttpSession session,
            @RequestBody IdDTO idDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        projectInviteService.cancel(getSessionUser(session), idDTO.id);
        logProcessingTimeFromStartTime(startTime, "cancel", idDTO.id);
        return new JsonContainer<Void, Void>();
    }
}
