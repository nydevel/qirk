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
package org.wrkr.clb.statistics.repo.model.feedback;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Feedback {

    @JsonIgnore
    public static final String SENDER_ID = "sender_id";
    @JsonIgnore
    public static final String SENDER_EMAIL = "sender_email";
    @JsonIgnore
    public static final String FEEDBACK = "feedback";
    @JsonIgnore
    public static final String CREATED_AT = "created_at";

    @JsonProperty(value = SENDER_ID)
    public Long senderId;
    @JsonProperty(value = SENDER_EMAIL)
    public String senderEmail;
    @JsonProperty(value = FEEDBACK)
    public String feedback;
    @JsonProperty(value = CREATED_AT)
    public String createdAt;
}
