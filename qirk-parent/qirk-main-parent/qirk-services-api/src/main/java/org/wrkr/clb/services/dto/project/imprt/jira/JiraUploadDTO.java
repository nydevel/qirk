package org.wrkr.clb.services.dto.project.imprt.jira;

import java.util.List;

import org.wrkr.clb.common.util.datetime.DateTimeWithEpochDTO;
import org.wrkr.clb.model.project.imprt.jira.ImportedJiraProject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JiraUploadDTO {

    public DateTimeWithEpochDTO timestamp;
    @JsonProperty(value = "archive_filename")
    public String archiveFilename;
    @JsonProperty(value = "imported_projects")
    public List<ImportedJiraProject> importedProjects;

    public JiraUploadDTO(long timestamp, String archiveName, List<ImportedJiraProject> importedProjects) {
        this.timestamp = new DateTimeWithEpochDTO(timestamp);
        this.archiveFilename = archiveName;
        this.importedProjects = importedProjects;
    }
}
