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

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;
import org.wrkr.clb.common.mail.EmailSentDTO;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.CurrentUserProfileDTO;
import org.wrkr.clb.services.dto.user.EmailAddressDTO;
import org.wrkr.clb.services.dto.user.LoginDTO;
import org.wrkr.clb.services.dto.user.PasswordChangeDTO;
import org.wrkr.clb.services.dto.user.PriofileUpdateDTO;

@Validated
public interface ProfileService {

    public EmailSentDTO resetPassword(@Valid EmailAddressDTO emailDTO) throws Exception;

    public void changePassword(User sessionUser, @Valid PasswordChangeDTO passwordDTO) throws Exception;

    public User getAccount(@Valid LoginDTO loginDTO);

    public User acceptLicense(User user);

    public CurrentUserProfileDTO getProfile(User sessionUser) throws Exception;

    public CurrentUserProfileDTO updateProfile(HttpSession session, User sessionUser, @Valid PriofileUpdateDTO profileDTO)
            throws Exception;

    public TokenAndIvDTO getNotificationToken(User sessionUser) throws Exception;

    public TokenAndIvDTO getCreditToken(User sessionUser) throws Exception;
}
