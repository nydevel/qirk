package org.wrkr.clb.common.crypto.token.notification;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NotificationTokenData {

    private static final ObjectWriter NOTIFICATION_TOKEN_DATA_WRITER = new ObjectMapper().writerFor(NotificationTokenData.class);

    public static final String SUBSCRIBER_ID = "subscriber_id";
    public static final String NOT_BEFORE = "not_before";
    public static final String NOT_ON_OR_AFTER = "not_after";

    @JsonProperty(value = SUBSCRIBER_ID)
    public Long subscriberId;
    @JsonProperty(value = NOT_BEFORE)
    public Long notBefore;
    @JsonProperty(value = NOT_ON_OR_AFTER)
    public Long notOnOrAfter;

    public NotificationTokenData() {
    }

    public NotificationTokenData(Map<String, Object> map) {
        subscriberId = (Long) map.get(SUBSCRIBER_ID);
        notBefore = (Long) map.get(NOT_BEFORE);
        notOnOrAfter = (Long) map.get(NOT_ON_OR_AFTER);
    }

    public String toJson() throws JsonProcessingException {
        return NOTIFICATION_TOKEN_DATA_WRITER.writeValueAsString(this);
    }
}
