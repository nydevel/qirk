package org.wrkr.clb.common.jms.message.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NewMemoMessage extends BaseStatisticsMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(NewMemoMessage.class);

    public static final String AUTHOR_USER_ID = "author_user_id";
    public static final String CREATED_AT = "created_at";

    @JsonProperty(value = AUTHOR_USER_ID)
    public long authorUserId;
    @JsonProperty(value = CREATED_AT)
    public long createdAt;

    public NewMemoMessage(long authorUserId, long createdAt) {
        super(BaseStatisticsMessage.Code.NEW_MEMO);
        this.authorUserId = authorUserId;
        this.createdAt = createdAt;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
