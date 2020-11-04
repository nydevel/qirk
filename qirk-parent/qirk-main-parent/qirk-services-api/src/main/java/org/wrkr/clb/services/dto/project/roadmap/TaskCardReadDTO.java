package org.wrkr.clb.services.dto.project.roadmap;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.project.roadmap.TaskCard;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.project.task.ShortTaskDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskCardReadDTO extends IdDTO {

    @JsonProperty(value = "record_version")
    @JsonInclude(Include.NON_NULL)
    public Long recordVersion;

    public String name;

    public TaskCard.Status status;

    @JsonProperty(value = "created_at")
    public String createdAt;

    @JsonProperty(value = "archieved_at")
    @JsonInclude(Include.NON_NULL)
    public String archievedAt;

    @JsonInclude(Include.NON_NULL)
    public RoadReadDTO road;

    @JsonInclude(Include.NON_NULL)
    public List<ShortTaskDTO> tasks;

    public static TaskCardReadDTO fromEntity(TaskCard card) {
        TaskCardReadDTO dto = new TaskCardReadDTO();

        dto.id = card.getId();
        dto.recordVersion = card.getRecordVersion();
        dto.name = card.getName();
        dto.status = card.getStatus();
        dto.createdAt = card.getCreatedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        if (card.getArchievedAt() != null) {
            dto.archievedAt = card.getArchievedAt().format(DateTimeUtils.WEB_DATETIME_FORMATTER);
        }
        if (card.getRoad() != null) {
            dto.road = RoadReadDTO.fromEntityWithDeleted(card.getRoad());
        }

        return dto;
    }

    public static TaskCardReadDTO fromEntityWithTasks(TaskCard card) {
        TaskCardReadDTO dto = fromEntity(card);
        dto.tasks = ShortTaskDTO.fromEntitiesWithRecordVersionAndPriorityAndStatus(card.getTasks());
        return dto;
    }

    public static List<TaskCardReadDTO> fromEntities(List<TaskCard> cardList) {
        List<TaskCardReadDTO> dtoList = new ArrayList<TaskCardReadDTO>();
        for (TaskCard card : cardList) {
            dtoList.add(fromEntity(card));
        }
        return dtoList;
    }
}
