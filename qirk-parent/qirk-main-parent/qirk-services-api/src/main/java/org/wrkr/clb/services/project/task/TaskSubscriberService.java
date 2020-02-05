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
package org.wrkr.clb.services.project.task;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.UserIdsDTO;

public interface TaskSubscriberService {

    public void create(Long userId, Long taskId);

    public void create(User currentUser, @NotNull(message = "taskId in TaskSubscriberService must not be null") Long taskId);

    public List<Long> list(@NotNull(message = "taskId in TaskSubscriberService must not be null") Long taskId);

    public UserIdsDTO list(User currentUser,
            @NotNull(message = "taskId in TaskSubscriberService must not be null") Long taskId);

    public List<User> listWithEmail(Long taskId, NotificationSettings.Setting notifSetting);

    public void delete(Long userId, Long taskId);

    public void delete(User currentUser, @NotNull(message = "taskId in TaskSubscriberService must not be null") Long taskId);
}
