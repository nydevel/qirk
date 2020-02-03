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
package org.wrkr.clb.common.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;

public abstract class BaseMapper<T extends Object> implements RowMapper<T> {

    protected String tableName = "";

    public BaseMapper() {
    }

    public BaseMapper(String tableName) {
        this.tableName = tableName;
    }

    public String generateColumnAlias(String columnLabel) {
        return (tableName.isEmpty() ? columnLabel : tableName + "__" + columnLabel);
    }

    protected String generateSelectColumnStatement(String columnLabel) {
        return (tableName.isEmpty() ? columnLabel : tableName + "." + columnLabel + " AS " + generateColumnAlias(columnLabel));
    }

    public abstract String generateSelectColumnsStatement();

    private OffsetDateTime getOffsetDateTime(Timestamp timestamp, ZoneId zone) {
        if (timestamp == null) {
            return null;
        }

        Instant instant = timestamp.toInstant();
        return OffsetDateTime.ofInstant(instant, zone);
    }

    private OffsetDateTime getOffsetDateTime(Timestamp timestamp) {
        return getOffsetDateTime(timestamp, DateTimeUtils.DEFAULT_TIME_ZONE_ID);
    }

    protected OffsetDateTime getOffsetDateTime(ResultSet rs, String columnLabel, ZoneId zone) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnLabel);
        return getOffsetDateTime(timestamp, zone);
    }

    protected OffsetDateTime getOffsetDateTime(ResultSet rs, String columnLabel) throws SQLException {
        return getOffsetDateTime(rs, columnLabel, DateTimeUtils.DEFAULT_TIME_ZONE_ID);
    }

    protected OffsetDateTime getOffsetDateTime(Map<String, Object> result, String columnLabel) {
        Timestamp timestamp = (Timestamp) result.get(columnLabel);
        return getOffsetDateTime(timestamp);
    }
}
