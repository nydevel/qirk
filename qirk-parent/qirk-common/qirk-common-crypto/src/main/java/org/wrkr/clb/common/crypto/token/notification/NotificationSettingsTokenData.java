package org.wrkr.clb.common.crypto.token.notification;

import org.wrkr.clb.common.util.strings.JSONifiable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NotificationSettingsTokenData implements JSONifiable {

    private static final ObjectWriter NOTIFICATION_TOKEN_DATA_WRITER = new ObjectMapper()
            .writerFor(NotificationSettingsTokenData.class);

    public static final String USER_EMAIL = "user_email";
    public static final String TYPE = "type";

    @JsonProperty(value = USER_EMAIL)
    public String userEmail;
    @JsonProperty(value = TYPE)
    public String type;

    public NotificationSettingsTokenData(String userEmail, String type) {
        this.userEmail = userEmail;
        this.type = type;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return NOTIFICATION_TOKEN_DATA_WRITER.writeValueAsString(this);
    }
}
