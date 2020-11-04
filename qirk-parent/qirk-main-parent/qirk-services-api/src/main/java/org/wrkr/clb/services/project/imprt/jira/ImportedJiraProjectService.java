package org.wrkr.clb.services.project.imprt.jira;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProject;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.project.imprt.ImportStatusDTO;
import org.wrkr.clb.services.dto.project.imprt.jira.JiraProjectImportDTO;

public interface ImportedJiraProjectService {

    public Map<Long, List<ImportedJiraProject>> mapTimestampToImportedProject();

    public void importNewProject(User importingUser,
            Document entitiesDoc, ImportedJiraProject importedProject, JiraProjectImportDTO importDTO,
            String uploadFolderPath, char uploadFolderDelimeter) throws Exception;

    public ImportStatusDTO importProjectUpdate(User importingUser,
            Document entitiesDoc, ImportedJiraProject importedProject, JiraProjectImportDTO importDTO,
            String uploadFolderPath, char uploadFolderDelimeter) throws Exception;
}
