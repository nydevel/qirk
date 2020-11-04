package org.wrkr.clb.services.dto.user;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationSettingsDTO {

    @NotNull(message = "token in NotificationSettingsDTO must not be null")
    public String token;
    @NotNull(message = "notIVification_settings in NotificationSettingsDTO must not be null")
    public String IV;

    @JsonProperty(value = "task_created")
    @NotNull(message = "task_created in NotificationSettingsDTO must not be null")
    public Boolean taskCreated;
    @JsonProperty(value = "task_updated")
    @NotNull(message = "task_updated in NotificationSettingsDTO must not be null")
    public Boolean taskUpdated;
    @JsonProperty(value = "task_commented")
    @NotNull(message = "task_commented in NotificationSettingsDTO must not be null")
    public Boolean taskCommented;
}
