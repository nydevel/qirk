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
package org.wrkr.clb.services.dto.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailAddressDTO {

    @JsonProperty(value = "email")
    @NotEmpty(message = "email in EmailDTO must not be empty")
    @Email(message = "email in EmailDTO must be valid")
    public String emailAddress;

    public static EmailAddressDTO fromEmail(String emailAddress) {
        EmailAddressDTO dto = new EmailAddressDTO();
        dto.emailAddress = emailAddress;
        return dto;
    }
}
