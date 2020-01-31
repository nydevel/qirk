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
package org.wrkr.clb.notification.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wrkr.clb.notification.model.NotificationLastCheck;
import org.wrkr.clb.notification.repo.NotificationLastCheckRepo;
import org.wrkr.clb.notification.services.dto.LastCheckDTO;


@Service
public class NotificationLastCheckService {

    @Autowired
    private NotificationLastCheckRepo notificationLastCheckRepo;

    public LastCheckDTO updateLastCheckTimestamp(Long subscriberId) {
        if (subscriberId == null) {
            return null;
        }

        long lastCheckTimestamp = System.currentTimeMillis();
        NotificationLastCheck lastCheck = new NotificationLastCheck();
        lastCheck.setSubscriberId(subscriberId);
        lastCheck.setLastCheckTimestamp(lastCheckTimestamp);

        boolean updated = false;
        if (notificationLastCheckRepo.exists(subscriberId)) {
            updated = notificationLastCheckRepo.update(lastCheck);
        } else {
            updated = notificationLastCheckRepo.save(lastCheck);
        }

        if (updated) {
            return new LastCheckDTO(lastCheckTimestamp);
        }
        return getLastCheckTimestamp(subscriberId);
    }

    public LastCheckDTO getLastCheckTimestamp(Long subscriberId) {
        if (subscriberId == null) {
            return null;
        }

        Long lastCheck = notificationLastCheckRepo.getLastCheckTimestampBySubscriberId(subscriberId);
        return new LastCheckDTO(lastCheck);
    }
}
