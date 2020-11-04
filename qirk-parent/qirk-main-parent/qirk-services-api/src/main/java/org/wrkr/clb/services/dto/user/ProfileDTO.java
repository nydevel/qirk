package org.wrkr.clb.services.dto.user;

import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.User;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileDTO extends PublicUserDTO {

    @JsonProperty("email")
    public String emailAddress;

    @JsonProperty(value = "notification_settings")
    public NotificationSettings notificationSettings;

    public static ProfileDTO fromEntity(User user) {
        ProfileDTO dto = new ProfileDTO();

        dto.id = user.getId();
        dto.username = user.getUsername();
        dto.fullName = user.getFullName();
        dto.emailAddress = user.getEmailAddress();
        dto.notificationSettings = user.getNotificationSettings();

        return dto;
    }
}
