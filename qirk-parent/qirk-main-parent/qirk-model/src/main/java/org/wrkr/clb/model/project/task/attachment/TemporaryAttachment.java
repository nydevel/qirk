package org.wrkr.clb.model.project.task.attachment;

import org.wrkr.clb.model.BaseEntity;
import org.wrkr.clb.model.project.Project;

public class TemporaryAttachment extends BaseEntity {

    private String uuid;

    private String filename;

    private String path;

    private Project project;
    private Long projectId;

    private long createdAt;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getProjectId() {
        if (projectId == null) {
            return (project == null ? null : project.getId());
        }
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
