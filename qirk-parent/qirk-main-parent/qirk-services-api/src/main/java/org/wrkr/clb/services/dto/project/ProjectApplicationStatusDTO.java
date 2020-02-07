package org.wrkr.clb.services.dto.project;

import org.wrkr.clb.model.project.ApplicationStatus;
import org.wrkr.clb.model.project.ProjectApplication;
import org.wrkr.clb.services.dto.IdDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Evgeny Poreykin
 *
 */
public class ProjectApplicationStatusDTO extends IdDTO {

    @JsonInclude(Include.NON_NULL)
    public ApplicationStatus status;

    public static ProjectApplicationStatusDTO fromEntity(ProjectApplication application) {
        ProjectApplicationStatusDTO dto = new ProjectApplicationStatusDTO();

        if (application != null) {
            dto.id = application.getId();
            dto.status = application.getStatus();
        }

        return dto;
    }
}
