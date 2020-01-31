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
package org.wrkr.clb.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MoveDTO extends IdDTO {

    @JsonProperty(value = "old_previous")
    public Long oldPreviousId;

    @JsonProperty(value = "old_next")
    public Long oldNextId;

    @JsonProperty(value = "previous")
    public Long previousId;

    @JsonProperty(value = "next")
    public Long nextId;
}
