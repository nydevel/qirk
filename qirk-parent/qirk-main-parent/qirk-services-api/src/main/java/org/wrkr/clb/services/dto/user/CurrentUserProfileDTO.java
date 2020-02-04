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
package org.wrkr.clb.services.dto.user;

import java.util.List;

import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.Tag;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentUserProfileDTO extends PublicProfileDTO {

    public String email;

    @JsonProperty(value = "notification_settings")
    public NotificationSettings notificationSettings;

    public static CurrentUserProfileDTO fromEntity(User user, List<Tag> tags, List<Language> languages) {
        CurrentUserProfileDTO dto = new CurrentUserProfileDTO();

        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.fullName = user.getFullName();
        dto.dontRecommend = user.isDontRecommend();
        dto.email = user.getEmailAddress();
        dto.about = user.getAbout();
        dto.notificationSettings = user.getNotificationSettings();
        dto.tags = tags;
        dto.languages = languages;
        // links = user.getLinks();

        return dto;
    }

    public static CurrentUserProfileDTO fromEntity(User user) {
        return fromEntity(user, user.getTags(), user.getLanguages());
    }
}
