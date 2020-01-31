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
package org.wrkr.clb.common.util.datetime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static final ZoneId DEFAULT_TIME_ZONE_ID = ZoneId.of("UTC");
    public static final ZoneId CRDT_TIME_ZONE_ID = ZoneId.of("UTC-6");

    public static final DateTimeFormatter WEB_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SZ");
    public static final DateTimeFormatter PSQL_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSx");
    public static final SimpleDateFormat JIRA_DATETIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static final int SYSTEM_DEFAULT_OFFSET_SECONDS = ZoneId.systemDefault().getRules().getOffset(Instant.now())
            .getTotalSeconds();
    public static final long SYSTEM_DEFAULT_OFFSET_MILLIS = SYSTEM_DEFAULT_OFFSET_SECONDS * 1000L;

    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public static OffsetDateTime now() {
        return OffsetDateTime.now(DEFAULT_TIME_ZONE_ID);
    }

    public static OffsetDateTime convertEpochMilliToOffsetDateTime(long epochMilli) {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), DEFAULT_TIME_ZONE_ID);
    }

    public static OffsetDateTime parseToOffsetDateTimeAtUTC(SimpleDateFormat formatter, String source) throws ParseException {
        return formatter.parse(source).toInstant().atOffset(ZoneOffset.UTC);
    }
}
