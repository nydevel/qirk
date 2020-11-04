package org.wrkr.clb.services.dto.user;

import org.wrkr.clb.model.user.UserFavorite;
import org.wrkr.clb.services.dto.IdDTO;
import org.wrkr.clb.services.dto.project.ProjectNameAndUiIdDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserFavoriteReadDTO extends IdDTO {

    public ProjectNameAndUiIdDTO project;

    @JsonProperty(value = "can_create_task")
    @JsonInclude(Include.NON_NULL)
    public Boolean canCreateTask;

    public static UserFavoriteReadDTO fromEntity(UserFavorite userFavorite) {
        UserFavoriteReadDTO dto = new UserFavoriteReadDTO();

        dto.id = userFavorite.getId();
        dto.project = ProjectNameAndUiIdDTO.fromEntity(userFavorite.getProject());

        return dto;
    }
}
