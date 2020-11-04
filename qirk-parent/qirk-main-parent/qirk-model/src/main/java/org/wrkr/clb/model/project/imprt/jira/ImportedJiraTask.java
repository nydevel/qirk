package org.wrkr.clb.model.project.imprt.jira;

public class ImportedJiraTask {

    private Long projectId;

    private Long taskId;

    private Long jiraTaskId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getJiraTaskId() {
        return jiraTaskId;
    }

    public void setJiraTaskId(Long jiraTaskId) {
        this.jiraTaskId = jiraTaskId;
    }
}
