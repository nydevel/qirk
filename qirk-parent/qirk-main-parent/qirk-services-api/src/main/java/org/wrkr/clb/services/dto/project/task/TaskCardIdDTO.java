package org.wrkr.clb.services.dto.project.task;

import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.services.dto.VersionedEntityDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskCardIdDTO extends VersionedEntityDTO {

    @JsonProperty(value = "card")
    public Long cardId;

    public static TaskCardIdDTO fromEntity(Task task) {
        TaskCardIdDTO dto = new TaskCardIdDTO();

        dto.id = task.getId();
        dto.recordVersion = task.getRecordVersion();
        dto.cardId = task.getCardId();

        return dto;
    }
}
