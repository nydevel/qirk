package org.wrkr.clb.services.dto.user;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.wrkr.clb.common.util.strings.RegExpPattern;
import org.wrkr.clb.model.user.NotificationSettings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PriofileUpdateDTO {

    @JsonProperty(value = "full_name")
    @NotBlank(message = "full_name in PriofileUpdateDTO must not be blank")
    @Pattern(regexp = RegExpPattern.NO_AT_SIGN + "+", message = "full_name in PriofileUpdateDTO must not contain @")
    public String fullName;

    @NotNull(message = "about in PriofileUpdateDTO must not be null")
    public String about;

    @JsonProperty(value = "notification_settings")
    @NotNull(message = "notification_settings in PriofileUpdateDTO must not be null")
    public NotificationSettings notificationSettings = new NotificationSettings();

    @JsonProperty(value = "tags")
    @NotNull(message = "tags in PriofileUpdateDTO must not be null")
    public Set<String> tagNames = new HashSet<String>();

    @JsonProperty(value = "languages")
    @NotNull(message = "languages in PriofileUpdateDTO must not be null")
    public Set<Long> languageIds = new HashSet<Long>();
}
