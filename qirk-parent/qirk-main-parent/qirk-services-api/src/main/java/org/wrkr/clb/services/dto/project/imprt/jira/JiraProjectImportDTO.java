package org.wrkr.clb.services.dto.project.imprt.jira;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.project.task.TaskPriority;
import org.wrkr.clb.model.project.task.TaskStatus;
import org.wrkr.clb.model.project.task.TaskType;
import org.wrkr.clb.model.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JiraProjectImportDTO {

    public static enum OverrideMode {
        NONE("NONE"),
        NON_UPDATED("NON_UPDATED"),
        ALL("ALL");

        @SuppressWarnings("unused")
        private final String nameCode;

        OverrideMode(final String nameCode) {
            this.nameCode = nameCode;
        }

        public boolean updateTask(Task task, OffsetDateTime lastImportAt) {
            switch (this) {
                case NONE:
                    return false;
                case NON_UPDATED:
                    return (!task.getUpdatedAt().isAfter(lastImportAt));
                case ALL:
                default:
                    return true;
            }
        }
    }

    @NotNull(message = "timestamp in JiraProjectImportDTO must not be null")
    public Long timestamp;

    @JsonProperty(value = "projects")
    @NotEmpty(message = "projects in JiraProjectImportDTO must not be empty")
    public Set<String> projectIds;
    @JsonProperty(value = "project_mapping")
    @NotNull(message = "project_mapping in JiraProjectImportDTO must not be null")
    public Map<String, Long> jiraProjectIdToQirkProjectId = new HashMap<String, Long>();

    // @JsonProperty(value = "private") TODO turn on
    @JsonIgnore
    @NotNull(message = "private in ProjectDTO must not be null")
    public boolean isPrivate = true;

    @JsonProperty(value = "override_tasks")
    @NotNull(message = "override_tasks in JiraProjectImportDTO must not be null")
    public OverrideMode overrideTasks = OverrideMode.ALL;

    @JsonProperty(value = "types")
    @NotNull(message = "types in JiraProjectImportDTO must not be null")
    public Map<String, String> jiraTypeIdToQirkTypeNameCode;

    @JsonProperty(value = "priorities")
    @NotNull(message = "priorities in JiraProjectImportDTO must not be null")
    public Map<String, String> jiraPriorityIdToQirkPriorityNameCode;

    @JsonProperty(value = "statuses")
    @NotNull(message = "statuses in JiraProjectImportDTO must not be null")
    public Map<String, String> jiraStatusIdToQirkStatusNameCode;

    @JsonProperty(value = "members")
    @NotNull(message = "members in JiraProjectImportDTO must not be null")
    public Map<String, Long> jiraUserNameToQirkOrgUserId;

    // fields filled during service execution

    @JsonIgnore
    public Map<String, TaskType> jiraTypeIdToQirkType;
    @JsonIgnore
    public TaskType defaultType;

    @JsonIgnore
    public Map<String, TaskPriority> jiraPriorityIdToQirkPriority;
    @JsonIgnore
    public TaskPriority defaultPriority;

    @JsonIgnore
    public Map<String, TaskStatus> jiraStatusIdToQirkStatus;
    @JsonIgnore
    public TaskStatus defaultStatus;

    @JsonIgnore
    public Map<String, String> jiraUserNameToJiraUserId;
    @JsonIgnore
    public Map<String, User> jiraUserNameToQirkUser;
    @JsonIgnore
    public Map<String, ProjectMember> jiraUserNameToQirkProjectMember;
}
