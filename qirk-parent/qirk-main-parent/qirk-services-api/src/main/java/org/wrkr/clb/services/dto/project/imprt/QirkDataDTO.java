package org.wrkr.clb.services.dto.project.imprt;

import java.util.List;

import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.project.ProjectNameAndUiIdDTO;
import org.wrkr.clb.services.dto.user.AccountUserDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QirkDataDTO {

    @JsonProperty(value = "imported_projects")
    public List<ProjectNameAndUiIdDTO> importedProjects;
    public List<AccountUserDTO> users;

    public List<TaskAttributeDTO> types;
    public List<TaskAttributeDTO> priorities;
    public List<TaskAttributeDTO> statuses;

    public static QirkDataDTO fromEntities(List<Project> projectList, List<User> userList) {
        QirkDataDTO dto = new QirkDataDTO();

        dto.importedProjects = ProjectNameAndUiIdDTO.fromEntities(projectList);
        dto.users = AccountUserDTO.fromEntitiesWithEmail(userList);

        dto.types = TaskAttributeDTO.fromTypes(TaskType.Type.values());
        dto.priorities = TaskAttributeDTO.fromPriorities(TaskPriority.Priority.values());
        dto.statuses = TaskAttributeDTO.fromStatuses(TaskStatus.Status.values());

        return dto;
    }
}
