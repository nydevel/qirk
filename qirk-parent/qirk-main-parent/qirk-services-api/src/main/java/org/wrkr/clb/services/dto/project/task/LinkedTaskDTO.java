package org.wrkr.clb.services.dto.project.task;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.project.ProjectMemberUserDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkedTaskDTO extends IdDTO {

    public Long number;

    public String summary;

    public ProjectMemberUserDTO assignee;

    @JsonProperty(value = "task_status")
    @JsonInclude(Include.NON_NULL)
    public TaskStatus taskStatus;

    public static LinkedTaskDTO fromEntity(Task task) {
        LinkedTaskDTO dto = new LinkedTaskDTO();

        dto.id = task.getId();
        dto.number = task.getNumber();
        dto.summary = task.getSummary();
        if (task.getAssignee() != null) {
            dto.assignee = ProjectMemberUserDTO.fromEntity(task.getAssignee());
        }
        dto.taskStatus = task.getStatus();

        return dto;
    }

    public static List<LinkedTaskDTO> fromEntities(List<Task> taskList) {
        List<LinkedTaskDTO> dtoList = new ArrayList<LinkedTaskDTO>(taskList.size());
        for (Task task : taskList) {
            dtoList.add(fromEntity(task));
        }
        return dtoList;
    }
}
