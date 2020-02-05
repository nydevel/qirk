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
package org.wrkr.clb.common.jms.message.statistics;

import org.wrkr.clb.common.util.strings.JsonUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

public class NotificationUnsubscriptionMessage extends BaseStatisticsMessage {

    public static final String USER_ID = "user_id";
    public static final String NOTIFICATION_TYPE = "notification_type";

    @JsonProperty(value = USER_ID)
    public long userId;
    @JsonProperty(value = NOTIFICATION_TYPE)
    public String notificationType;

    public NotificationUnsubscriptionMessage(long userId, String notificationType) {
        super(BaseStatisticsMessage.Code.NOTIFICATION_UNSUBSCRIPTION);
        this.userId = userId;
        this.notificationType = notificationType;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return JsonUtils.convertObjectToJson(this);
    }
}
