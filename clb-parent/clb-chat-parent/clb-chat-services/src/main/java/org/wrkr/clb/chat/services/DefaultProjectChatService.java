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
package org.wrkr.clb.chat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wrkr.clb.chat.services.cassandra.CassandraChatService;
import org.wrkr.clb.chat.services.cassandra.CassandraProjectChatService;
import org.wrkr.clb.chat.services.mariadb.MariaDBChatService;
import org.wrkr.clb.chat.services.mariadb.MariaDBProjectChatService;


@Component
public class DefaultProjectChatService extends DefaultChatService implements ProjectChatService {

    @Autowired
    private CassandraProjectChatService cassandraService;

    @Deprecated
    @Autowired(required = false)
    private MariaDBProjectChatService mariaDBService;

    @Override
    protected CassandraChatService getCassandraService() {
        return cassandraService;
    }

    @Deprecated
    @Override
    protected MariaDBChatService getMariaDBService() {
        return mariaDBService;
    }
}
