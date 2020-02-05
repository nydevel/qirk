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
package org.wrkr.clb.common.util.datetime;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DateTimeWithEpochDTO {

    public String iso8601;

    @JsonProperty(value = "epoch_milli")
    public long epochMilli;

    public DateTimeWithEpochDTO(OffsetDateTime datetime) {
        iso8601 = datetime.format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        epochMilli = datetime.toInstant().toEpochMilli();
    }

    public DateTimeWithEpochDTO(long epochMilli) {
        this.epochMilli = epochMilli;
        this.iso8601 = DateTimeUtils.convertEpochMilliToOffsetDateTime(epochMilli).format(DateTimeUtils.WEB_DATETIME_FORMATTER);
    }
}
