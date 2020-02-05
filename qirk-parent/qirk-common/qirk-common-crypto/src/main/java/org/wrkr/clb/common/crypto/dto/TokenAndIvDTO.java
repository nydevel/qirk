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
package org.wrkr.clb.common.crypto.dto;

import java.util.Base64;

/**
 * @summary DTO used to return the result of encryption by TokenGenerator
 * 
 */
public class TokenAndIvDTO {

    public String token;

    public String IV;

    public TokenAndIvDTO() {
    }

    public TokenAndIvDTO(byte[] token, byte[] IV) {
        this.token = Base64.getEncoder().encodeToString(token);
        this.IV = Base64.getEncoder().encodeToString(IV);
    }
}
