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
package org.wrkr.clb.common.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DriverTimeoutException;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;

public abstract class BaseRepo {

    private static final int DEFAULT_RETRIES_COUNT = 2;

    protected abstract CqlSession getSession();

    protected ResultSet execute(BoundStatement statement, int retriesCount) {
        int retryNumber = 0;
        DriverTimeoutException caughtException;
        do {
            try {
                return getSession().execute(statement);
            } catch (DriverTimeoutException e) {
                retryNumber++;
                caughtException = e;
            }
        } while (retryNumber < retriesCount);
        throw caughtException;
    }

    protected ResultSet execute(BoundStatement statement) {
        return execute(statement, DEFAULT_RETRIES_COUNT);
    }
}
