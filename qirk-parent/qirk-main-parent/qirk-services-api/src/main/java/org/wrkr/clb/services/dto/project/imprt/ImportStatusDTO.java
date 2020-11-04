package org.wrkr.clb.services.dto.project.imprt;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImportStatusDTO {

    public static enum Status {
        CREATED("CREATED"),
        UPDATED("UPDATED"),
        PARTIALLY_UPDATED("PARTIALLY_UPDATED"),
        WRONG_MAPPING("NOT_FOUND"),
        NOT_FOUND("WRONG_MAPPING"),
        CREATE_FAILED("CREATE_FAILED"),
        UPDATE_FAILED("UPDATE_FAILED");

        @SuppressWarnings("unused")
        private final String nameCode;

        Status(final String nameCode) {
            this.nameCode = nameCode;
        }
    }

    @JsonProperty("jira_project")
    public String jiraProjectId;

    public Status status;

    @JsonProperty("error_code")
    @JsonInclude(Include.NON_NULL)
    public String statusCode;

    @JsonProperty("failed_task_numbers")
    @JsonInclude(Include.NON_EMPTY)
    public List<Long> failedTaskNumbers;

    public ImportStatusDTO(String jiraProjectId, Status status) {
        this.jiraProjectId = jiraProjectId;
        this.status = status;
    }

    public ImportStatusDTO(String jiraProjectId, Status status, List<Long> failedTaskNumbers) {
        this.jiraProjectId = jiraProjectId;
        this.status = status;
        this.failedTaskNumbers = failedTaskNumbers;
    }

    public ImportStatusDTO(String jiraProjectId, Status status, String statusCode) {
        this.jiraProjectId = jiraProjectId;
        this.status = status;
        this.statusCode = statusCode;
    }
}
