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

    protected String tableAlias = "";

    public BaseMapper() {
    }

    public BaseMapper(String tableName) {
        this.tableAlias = tableName;
    }

    public String generateColumnAlias(String columnLabel) {
        return (tableAlias.isEmpty() ? columnLabel : tableAlias + "__" + columnLabel);
    }

    protected String generateSelectColumnStatement(String columnLabel) {
        return (tableAlias.isEmpty() ? columnLabel : tableAlias + "." + columnLabel + " AS " + generateColumnAlias(columnLabel));
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
