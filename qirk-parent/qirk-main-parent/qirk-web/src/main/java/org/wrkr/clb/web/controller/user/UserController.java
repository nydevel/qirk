/*
 * Copyright  
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
package org.wrkr.clb.web.controller.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.IdOrUiIdDTO;
import org.wrkr.clb.services.dto.user.ActivationDTO;
import org.wrkr.clb.services.dto.user.CurrentUserProfileDTO;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;
import org.wrkr.clb.services.dto.user.PasswordChangeDTO;
import org.wrkr.clb.services.dto.user.PriofileUpdateDTO;
import org.wrkr.clb.services.dto.user.PublicProfileDTO;
import org.wrkr.clb.services.dto.user.PublicUserDTO;
import org.wrkr.clb.services.dto.user.PublicUserMembershipDTO;
import org.wrkr.clb.services.dto.user.RegisterNoPasswordDTO;
import org.wrkr.clb.services.security.SecurityService;
import org.wrkr.clb.services.user.AuthnService;
import org.wrkr.clb.services.user.ProfileRetryWrapperService;
import org.wrkr.clb.services.user.ProfileService;
import org.wrkr.clb.services.user.RegistrationRetryWrapperService;
import org.wrkr.clb.services.user.RegistrationService;
import org.wrkr.clb.services.user.UserService;
import org.wrkr.clb.web.controller.BaseAuthenticationExceptionHandlerController;
import org.wrkr.clb.web.http.Header;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController extends BaseAuthenticationExceptionHandlerController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RegistrationRetryWrapperService registrationRWService;

    @Autowired
    private AuthnService authnService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRetryWrapperService profileRWService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @GetMapping(value = "check-email")
    public JsonContainer<ExistsDTO, Void> checkEmail(@SuppressWarnings("unused") HttpSession session,
            @RequestParam(name = "email") String email,
            @RequestParam(name = "exclude_disabled", required = false, defaultValue = "false") Boolean excludeDisabled) {
        long startTime = System.currentTimeMillis();
        ExistsDTO dto = registrationService.checkEmail(email, excludeDisabled);
        logProcessingTimeFromStartTime(startTime, "checkEmail", excludeDisabled);
        return new JsonContainer<ExistsDTO, Void>(dto);
    }

    @GetMapping(value = "check-username")
    public JsonContainer<ExistsDTO, Void> checkUsername(@SuppressWarnings("unused") HttpSession session,
            @RequestParam(name = "username") String username) {
        long startTime = System.currentTimeMillis();
        ExistsDTO dto = registrationService.checkUsername(username);
        logProcessingTimeFromStartTime(startTime, "checkUsername", username);
        return new JsonContainer<ExistsDTO, Void>(dto);
    }

    @PostMapping(value = "register")
    public JsonContainer<EmailSentDTO, Void> register(@SuppressWarnings("unused") HttpSession session,
            @RequestBody RegisterNoPasswordDTO emailDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        EmailSentDTO dto = registrationRWService.register(emailDTO);
        logProcessingTimeFromStartTime(startTime, "register");
        return new JsonContainer<EmailSentDTO, Void>(dto);
    }

    @Deprecated
    @PostMapping(value = "register-no-password")
    public JsonContainer<EmailSentDTO, Void> registerNoPassword(HttpSession session,
            @RequestBody RegisterNoPasswordDTO emailDTO) throws Exception {
        return register(session, emailDTO);
    }

    @Deprecated
    @PostMapping(value = "activate")
    public JsonContainer<Void, Void> activate(
            HttpServletRequest request, HttpServletResponse response, HttpSession session,
            @RequestBody ActivationDTO activationDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        User user = registrationRWService.activate(request, activationDTO);
        response = authnService.login(response, session, user, request.getHeader(Header.X_FORWARDED_FOR));
        logProcessingTimeFromStartTime(startTime, "activate");
        return new JsonContainer<Void, Void>();
    }

    @Deprecated
    @PostMapping(value = "activate-no-password")
    public JsonContainer<Void, Void> activateNoPassword(
            HttpServletRequest request, HttpServletResponse response, HttpSession session,
            @RequestBody ActivationDTO activationDTO) throws Exception {
        return activate(request, response, session, activationDTO);
    }

    @PostMapping(value = "accept-license")
    public JsonContainer<Void, Void> acceptLicense(HttpSession session) throws Exception {
        long startTime = System.currentTimeMillis();
        User currentUser = getSessionUser(session);
        securityService.isAuthenticated(currentUser);
        profileService.acceptLicense(currentUser);
        logProcessingTimeFromStartTime(startTime, "acceptLicense");
        return new JsonContainer<Void, Void>();
    }

    @PostMapping(value = "reset-password")
    public JsonContainer<EmailSentDTO, Void> resetPassword(@SuppressWarnings("unused") HttpSession session,
            @RequestBody EmailAddressDTO emailDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        EmailSentDTO dto = profileService.resetPassword(emailDTO);
        logProcessingTimeFromStartTime(startTime, "resetPassword");
        return new JsonContainer<EmailSentDTO, Void>(dto);
    }

    @PutMapping(value = "change-password")
    public JsonContainer<Void, Void> changePassword(HttpSession session,
            @RequestBody PasswordChangeDTO passwordDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        profileService.changePassword(getSessionUser(session), passwordDTO);
        logProcessingTimeFromStartTime(startTime, "changePassword");
        return new JsonContainer<Void, Void>();
    }

    @GetMapping(value = "profile")
    public JsonContainer<CurrentUserProfileDTO, Void> readProfile(HttpSession session)
            throws Exception {
        long startTime = System.currentTimeMillis();
        User currentUser = getSessionUser(session);
        CurrentUserProfileDTO dto = profileService.getProfile(currentUser);
        logProcessingTimeFromStartTime(startTime, "readProfile", currentUser);
        return new JsonContainer<CurrentUserProfileDTO, Void>(dto);
    }

    @PutMapping(value = "profile")
    public JsonContainer<CurrentUserProfileDTO, Void> updateProfile(HttpSession session,
            @RequestBody PriofileUpdateDTO profileDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        User currentUser = getSessionUser(session);
        CurrentUserProfileDTO dto = profileRWService.updateProfile(session, currentUser, profileDTO);
        logProcessingTimeFromStartTime(startTime, "updateProfile", currentUser);
        return new JsonContainer<CurrentUserProfileDTO, Void>(dto);
    }

    /*@formatter:off
    @GetMapping(value = "chat-token")
    public JsonContainer<TokenAndIvDTO, Void> getDialogChatToken(HttpSession session,
            @RequestParam(name = "id") long id) throws Exception {
        long startTime = System.currentTimeMillis();
        User user = getSessionUser(session);
        TokenAndIvDTO tokenDTO = userService.getDialogChatToken(user, id);
        logProcessingTimeFromStartTime(startTime, "getDialogChatToken", user, id);
        return new JsonContainer<TokenAndIvDTO, Void>(tokenDTO);
    }
    @formatter:on*/

    @GetMapping(value = "notification-token")
    public JsonContainer<TokenAndIvDTO, Void> getNotificationToken(HttpSession session) throws Exception {
        long startTime = System.currentTimeMillis();
        User user = getSessionUser(session);
        TokenAndIvDTO tokenDTO = profileService.getNotificationToken(user);
        logProcessingTimeFromStartTime(startTime, "getNotificationToken", user);
        return new JsonContainer<TokenAndIvDTO, Void>(tokenDTO);
    }

    @GetMapping(value = "credit-token")
    public JsonContainer<TokenAndIvDTO, Void> getCreditToken(HttpSession session) throws Exception {
        long startTime = System.currentTimeMillis();
        User user = getSessionUser(session);
        TokenAndIvDTO tokenDTO = profileService.getCreditToken(user);
        logProcessingTimeFromStartTime(startTime, "getCreditToken", user);
        return new JsonContainer<TokenAndIvDTO, Void>(tokenDTO);
    }

    @GetMapping(value = "/")
    public JsonContainer<PublicProfileDTO, Void> get(@SuppressWarnings("unused") HttpSession session,
            @RequestParam(name = "id") Long id) throws Exception {
        long startTime = System.currentTimeMillis();
        PublicProfileDTO profileDTO = userService.get(id);
        logProcessingTimeFromStartTime(startTime, "get", id);
        return new JsonContainer<PublicProfileDTO, Void>(profileDTO);
    }

    @GetMapping(value = "list")
    public JsonContainer<PublicUserDTO, Void> list(@SuppressWarnings("unused") HttpSession session,
            @RequestParam(name = "id", required = false, defaultValue = "") List<Long> ids)
            throws Exception {
        long startTime = System.currentTimeMillis();
        List<PublicUserDTO> userDTOList = userService.listByIds(ids);
        logProcessingTimeFromStartTime(startTime, "list", ids);
        return new JsonContainer<PublicUserDTO, Void>(userDTOList);
    }

    /*@formatter:off
    @GetMapping(value = "search")
    public JsonContainer<PublicUserDTO, Void> search(@SuppressWarnings("unused") HttpSession session,
            @RequestParam(name = "prefix") String prefix,
            @RequestParam(name = "include_tags", required = false, defaultValue = "false") boolean includeTags) throws Exception {
        long startTime = System.currentTimeMillis();
        List<PublicUserDTO> userDTOList = userService.search(prefix.strip(), includeTags);
        logProcessingTimeFromStartTime(startTime, "search", prefix, includeTags);
        return new JsonContainer<PublicUserDTO, Void>(userDTOList);
    }
    @formatter:on*/

    @GetMapping(value = "search-for-organization")
    public JsonContainer<PublicUserDTO, Void> searchForOrganization(HttpSession session,
            @RequestParam(name = "prefix") String searchValue,
            @RequestParam(name = "organization_id", required = false) Long organizationId,
            @RequestParam(name = "organization_ui_id", required = false) String organizationUiId) throws Exception {
        long startTime = System.currentTimeMillis();
        List<PublicUserDTO> userDTOList = userService.searchForOrganization(getSessionUser(session), searchValue.strip(),
                new IdOrUiIdDTO(organizationId, organizationUiId));
        logProcessingTimeFromStartTime(startTime, "searchForOrganization", organizationId, organizationUiId);
        return new JsonContainer<PublicUserDTO, Void>(userDTOList);
    }

    @GetMapping(value = "search-for-project")
    public JsonContainer<PublicUserMembershipDTO, Void> searchForProject(HttpSession session,
            @RequestParam(name = "prefix") String searchValue,
            @RequestParam(name = "project_id", required = false) Long projectId,
            @RequestParam(name = "project_ui_id", required = false) String projectUiId) throws Exception {
        long startTime = System.currentTimeMillis();
        List<PublicUserMembershipDTO> userDTOList = userService.searchForProject(getSessionUser(session), searchValue.strip(),
                new IdOrUiIdDTO(projectId, projectUiId));
        logProcessingTimeFromStartTime(startTime, "searchForProject", projectId, projectUiId);
        return new JsonContainer<PublicUserMembershipDTO, Void>(userDTOList);
    }
}
