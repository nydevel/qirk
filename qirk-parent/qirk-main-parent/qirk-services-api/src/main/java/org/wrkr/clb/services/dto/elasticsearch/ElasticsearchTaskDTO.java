package org.wrkr.clb.services.dto.elasticsearch;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.services.api.elasticsearch.ElasticsearchService.RequestType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ElasticsearchTaskDTO extends ElasticsearchEntityDTO {

    private static final ObjectWriter DTO_WRITER = new ObjectMapper().writerFor(ElasticsearchTaskDTO.class);

    public static final String PROJECT_ID = "project_id";
    public static final String NUMBER = "number";
    public static final String NUMBER_STRING = "number_string";

    public static final String SUMMARY = "summary";
    public static final String DESCRIPTION = "description";

    public static final String REPORTER = "reporter";
    public static final String ASSIGNEE = "assignee";

    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";

    public static final String TASK_TYPE = "task_type";
    public static final String TASK_PRIORITY = "task_priority";
    public static final String TASK_STATUS = "task_status";

    public static final String CARD = "card";
    public static final String HIDDEN = "hidden";

    public static final String HASHTAGS = "hashtags";

    @JsonProperty(value = PROJECT_ID)
    @JsonInclude(Include.NON_NULL)
    public Long projectId;
    @JsonProperty(value = NUMBER)
    @JsonInclude(Include.NON_NULL)
    public Long number;
    @JsonProperty(value = NUMBER_STRING)
    @JsonInclude(Include.NON_NULL)
    public String numberString;

    @JsonProperty(value = SUMMARY)
    public String summary;
    @JsonProperty(value = DESCRIPTION)
    public String description;

    @JsonProperty(value = REPORTER)
    @JsonInclude(Include.NON_NULL)
    public Long reporterId;
    @JsonProperty(value = ASSIGNEE)
    public Long assigneeId;

    @JsonProperty(value = CREATED_AT)
    @JsonInclude(Include.NON_NULL)
    public Long createdAt;
    @JsonProperty(value = UPDATED_AT)
    public Long updatedAt;

    @JsonProperty(value = TASK_TYPE)
    public String taskType;
    @JsonProperty(value = TASK_PRIORITY)
    public String taskPriority;
    @JsonProperty(value = TASK_STATUS)
    public String taskStatus;

    @JsonProperty(value = CARD)
    @JsonInclude(Include.NON_NULL)
    public Long card;
    @JsonProperty(value = HIDDEN)
    @JsonInclude(Include.NON_NULL)
    public boolean hidden;

    @JsonProperty(value = HASHTAGS)
    @JsonInclude(Include.NON_NULL)
    public List<String> hashtags;

    public static long getTaskCardId(Task task) {
        return (task.getCardId() == null ? -1L : task.getCardId());
    }

    public static ElasticsearchTaskDTO fromEntity(Task task, RequestType request) {
        ElasticsearchTaskDTO dto = new ElasticsearchTaskDTO();

        if (RequestType.INDEX.equals(request) || RequestType.DATASYNC_UPDATE.equals(request)) {
            dto.projectId = task.getProjectId();
            dto.number = task.getNumber();
            dto.numberString = dto.number.toString();
            dto.reporterId = task.getReporterId();
            dto.createdAt = task.getCreatedAt().toInstant().toEpochMilli();
        }

        dto.summary = task.getSummary();
        dto.description = task.getDescriptionMd();

        dto.assigneeId = task.getAssigneeId();

        dto.updatedAt = task.getUpdatedAt().toInstant().toEpochMilli();

        dto.taskType = task.getType().getNameCode().toString();
        dto.taskPriority = task.getPriority().getNameCode().toString();
        dto.taskStatus = task.getStatus().getNameCode().toString();

        if (!RequestType.UPDATE.equals(request)) {
            dto.card = getTaskCardId(task);
            dto.hidden = task.isHidden();
        }

        if (!RequestType.JIRA_UPDATE.equals(request)) {
            dto.hashtags = new ArrayList<String>(task.getHashtags().size());
            for (TaskHashtag hashtag : task.getHashtags()) {
                dto.hashtags.add(hashtag.getName());
            }
        }

        return dto;
    }

    @Override
    public String toJson() throws JsonProcessingException {
        return DTO_WRITER.writeValueAsString(this);
    }
}
