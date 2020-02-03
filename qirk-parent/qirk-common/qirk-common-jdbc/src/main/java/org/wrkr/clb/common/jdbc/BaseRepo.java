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

import java.sql.Array;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.JdbcUpdateAffectedIncorrectNumberOfRowsException;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseRepo {

    private static final Logger LOG = LoggerFactory.getLogger(BaseRepo.class);

    protected static String generateSelectAllStatement(String tableName) {
        return "SELECT * FROM " + tableName + ";";
    }

    protected static String generateSelectCountAllStatement(String tableName) {
        return "SELECT COUNT(*) FROM " + tableName + ";";
    }

    protected final String className = getClass().getSimpleName();

    private JdbcTemplate jdbcTemplate;

    protected JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    protected void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public abstract void setDataSource(DataSource dataSource);

    protected String insertNBindValues(String prefix, int amount, String suffix) {
        StringBuilder inClause = new StringBuilder(prefix);
        for (int i = 0; i < amount; i++) {
            if (i != 0) {
                inClause.append(',');
            }
            inClause.append('?');
        }
        return inClause.append(suffix).toString();
    }

    protected Array createArrayOf(String jdbcTypeName, Object[] array) {
        return getJdbcTemplate().execute((Connection c) -> c.createArrayOf(jdbcTypeName, array));
    }

    protected boolean exists(String sql, Object... args) {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> results = getJdbcTemplate().queryForList(sql, args);
        long resultTime = System.currentTimeMillis() - startTime;
        if (LOG.isDebugEnabled()) {
            LOG.debug("processed query [" + sql + "] in " + resultTime + " ms");
        } else {
            LOG.info("processed exists query in " + resultTime + " ms");
        }
        return (!results.isEmpty());
    }

    private void logProcessingTimeFromStartTime(long startTime, String sql, String typeName, int resultSize) {
        long resultTime = System.currentTimeMillis() - startTime;
        if (LOG.isDebugEnabled()) {
            LOG.debug("processed query [" + sql + "] with " + resultSize + " results in " + resultTime + " ms");
        } else {
            LOG.info("processed query for type " + typeName + " with " + resultSize + " results in " +
                    resultTime + " ms");
        }
    }

    protected <T extends Object> List<T> queryForList(String sql, Class<T> requiredType, Object... args)
            throws IncorrectResultSizeDataAccessException {
        long startTime = System.currentTimeMillis();
        List<T> results = getJdbcTemplate().queryForList(sql, requiredType, args);
        logProcessingTimeFromStartTime(startTime, sql, requiredType.getName(), results.size());
        return results;
    }

    protected <T extends Object> List<T> queryForList(String sql, Object[] args, Class<T> requiredType)
            throws IncorrectResultSizeDataAccessException {
        long startTime = System.currentTimeMillis();
        List<T> results = getJdbcTemplate().queryForList(sql, args, requiredType);
        logProcessingTimeFromStartTime(startTime, sql, requiredType.getName(), results.size());
        return results;
    }

    protected <T extends Object> List<T> queryForList(String sql, BaseMapper<T> rowMapper, Object... args)
            throws IncorrectResultSizeDataAccessException {
        long startTime = System.currentTimeMillis();
        List<T> results = getJdbcTemplate().query(sql, rowMapper, args);
        logProcessingTimeFromStartTime(startTime, sql, className, results.size());
        return results;
    }

    protected <T extends Object> List<T> queryForList(String sql, Object[] args, BaseMapper<T> rowMapper) {
        long startTime = System.currentTimeMillis();
        List<T> results = getJdbcTemplate().query(sql, args, rowMapper);
        logProcessingTimeFromStartTime(startTime, sql, className, results.size());
        return results;
    }

    private <T extends Object> T getSingleObjectOrNull(List<T> results) throws IncorrectResultSizeDataAccessException {
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }
        return results.get(0);
    }

    private <T extends Object> T getFirstObjectOrNull(List<T> results) throws IncorrectResultSizeDataAccessException {
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    protected <T extends Object> T queryForObjectOrNull(String sql, Class<T> requiredType, Object... args)
            throws IncorrectResultSizeDataAccessException {
        List<T> results = queryForList(sql, requiredType, args);
        return getSingleObjectOrNull(results);
    }

    protected <T extends Object> T queryForObjectOrNull(String sql, BaseMapper<T> rowMapper, Object... args)
            throws IncorrectResultSizeDataAccessException {
        List<T> results = queryForList(sql, rowMapper, args);
        return getSingleObjectOrNull(results);
    }

    protected <T extends Object> T queryForFirstObjectOrNull(String sql, Class<T> requiredType, Object... args)
            throws IncorrectResultSizeDataAccessException {
        List<T> results = queryForList(sql, requiredType, args);
        return getFirstObjectOrNull(results);
    }

    protected <T extends Object> T queryForFirstObjectOrNull(String sql, BaseMapper<T> rowMapper, Object... args)
            throws IncorrectResultSizeDataAccessException {
        List<T> results = queryForList(sql, rowMapper, args);
        return getFirstObjectOrNull(results);
    }

    protected void updateSingleRow(String sql, Object... args) throws JdbcUpdateAffectedIncorrectNumberOfRowsException {
        long startTime = System.currentTimeMillis();
        int affectedRows = getJdbcTemplate().update(sql, args);
        long resultTime = System.currentTimeMillis() - startTime;
        if (LOG.isDebugEnabled()) {
            LOG.debug("processed query [" + sql + "] affecting " + affectedRows + " rows in " + resultTime + " ms");
        } else {
            LOG.info("processed update query for repo " + className + " affecting " + affectedRows + " rows in "
                    + resultTime + " ms");
        }

        if (affectedRows > 1) {
            throw new JdbcUpdateAffectedIncorrectNumberOfRowsException(sql, 1, affectedRows);
        }
    }
}
