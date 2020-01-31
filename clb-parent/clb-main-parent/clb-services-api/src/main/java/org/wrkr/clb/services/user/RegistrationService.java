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
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.ExistsDTO;
import org.wrkr.clb.services.dto.user.ActivationDTO;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;
import org.wrkr.clb.services.dto.user.RegisterDTO;
import org.wrkr.clb.services.dto.user.RegisterNoPasswordDTO;


@Validated
public interface RegistrationService {

    public ExistsDTO checkEmail(
            @NotEmpty(message = "email in RegistrationService must not be empty") @Email(message = "email in RegistrationService must be valid") String email,
            boolean excludeDisabled);

    public ExistsDTO checkUsername(
            @NotNull(message = "username in RegistrationService must not be null") @Pattern(regexp = RegExpPattern.LOWER_CASE_SLUG
                    + "+", message = "username in RegistrationService must be lower case slug") String username);

    public User createUserWithEmailAndPasswordHash(
            @NotEmpty(message = "email in RegistrationService must not be empty") @Email(message = "email in RegistrationService must be valid") String email,
            @NotBlank(message = "passwordHash in RegistrationService must not be blank") String passwordHash,
            @NotNull(message = "username in RegistrationService must not be null") @Pattern(regexp = RegExpPattern.LOWER_CASE_SLUG
                    + "{1,25}", message = "username in RegistrationService must be lower case slug") String username,
            @NotBlank(message = "fullName in RegistrationService must not be blank") String fullName, boolean licenseAccepted)
            throws Exception;

    @Deprecated
    public EmailSentDTO register(HttpServletRequest request, @Valid RegisterDTO registerDTO) throws Exception;

    public EmailSentDTO register(@Valid RegisterNoPasswordDTO registerDTO) throws Exception;

    @Deprecated
    public EmailSentDTO resendConfirmationEmail(@Valid EmailAddressDTO emailDTO) throws Exception;

    @Deprecated
    public User activate(
            @NotNull(message = "token in RegistrationService must not be null") String token) throws Exception;

    public User activate(HttpServletRequest request, @Valid ActivationDTO activationDTO) throws Exception;
}
