package org.wrkr.clb.common.jms.message.statistics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ProjectDocUpdateMessage extends BaseStatisticsMessage {

    private static final ObjectWriter MESSAGE_WRITER = new ObjectMapper().writerFor(ProjectDocUpdateMessage.class);

    public static final String UPDATED_BY_USER_ID = "updated_by_user_id";

    @JsonProperty(value = UPDATED_BY_USER_ID)
    public long updatedByUserId;

    public ProjectDocUpdateMessage(long updatedByUserId) {
        super(BaseStatisticsMessage.Code.PROJECT_DOC_UPDATE);
        this.updatedByUserId = updatedByUserId;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return MESSAGE_WRITER.writeValueAsString(this);
    }
}
