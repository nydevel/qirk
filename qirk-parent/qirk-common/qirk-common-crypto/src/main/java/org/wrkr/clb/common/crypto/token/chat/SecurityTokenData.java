package org.wrkr.clb.common.crypto.token.chat;

import org.wrkr.clb.common.util.strings.JsonUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

public abstract class SecurityTokenData {

    public static final String SENDER_ID = "sender_id";
    public static final String WRITE = "write";
    public static final String NOT_BEFORE = "not_before";
    public static final String NOT_ON_OR_AFTER = "not_after";

    @JsonProperty(value = SENDER_ID)
    public Long senderId;
    @JsonProperty(value = WRITE)
    public boolean write = false;
    @JsonProperty(value = NOT_BEFORE)
    public Long notBefore;
    @JsonProperty(value = NOT_ON_OR_AFTER)
    public Long notOnOrAfter;

    public String toJson() throws JsonProcessingException {
        return JsonUtils.convertObjectToJson(this);
    }
}
