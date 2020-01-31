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
package org.wrkr.clb.notification.repo.cassandra;

import org.springframework.stereotype.Component;
import org.wrkr.clb.notification.repo.NotifDatabaseRepo;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;

@Component
public class CassandraNotifDatabaseRepo extends BaseNotifRepo implements NotifDatabaseRepo {

    private static final String SELECT_NOW = "SELECT now() FROM system.local";

    private PreparedStatement checkStatement;

    @Override
    public void afterPropertiesSet() throws Exception {
        checkStatement = session.prepare(SELECT_NOW);
    }

    @Override
    public void check() {
        BoundStatement statement = checkStatement.bind();
        ResultSet resultSet = session.execute(statement);

        if (!resultSet.wasApplied()) {
            throw new RuntimeException("Cassandra statement was not applied");
        }
    }
}
