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
package org.wrkr.clb.statistics.repo.user;

import javax.validation.constraints.NotNull;

import org.wrkr.clb.statistics.repo.BaseStatRepo;
import org.wrkr.clb.statistics.repo.model.user.NotificationUnsubscription_;

public class NotificationUnsubscriptionRepo extends BaseStatRepo {

    private static final String INSERT = "INSERT INTO " + NotificationUnsubscription_.TABLE_NAME + " " +
            "(" + NotificationUnsubscription_.userId + ", " + // 1
            NotificationUnsubscription_.notificationType + ") " + // 2
            "VALUES (?, ?);";

    public void save(@NotNull(message = "userId in UserRegistrationRepo must not be null") Long userId,
            @NotNull(message = "notificationType in UserRegistrationRepo must not be null") String notificationType) {
        getJdbcTemplate().update(INSERT,
                userId, notificationType);
    }
}
