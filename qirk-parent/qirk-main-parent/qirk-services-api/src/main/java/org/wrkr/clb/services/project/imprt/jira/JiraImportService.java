package org.wrkr.clb.services.project.imprt.jira;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.fileupload.FileItem;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.project.imprt.ImportStatusDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectMatchDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectImportDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraUploadDTO;

@Validated
public interface JiraImportService {

    public JiraUploadDTO uploadJiraImportFile(User currentUser,
            @NotNull(message = "file must not be null") FileItem file) throws Exception;

    public List<JiraUploadDTO> listUploads(User currentUser) throws Exception;

    public List<JiraProjectDTO> listProjects(User currentUser, long timestamp) throws Exception;

    public JiraProjectMatchDTO listProjectsData(User currentUser, long timestamp,
            @NotEmpty(message = "projectIds must not be empty") Set<String> projectIds) throws Exception;

    public List<ImportStatusDTO> importProjects(User currentUser, @Valid JiraProjectImportDTO importDTO) throws Exception;
}
