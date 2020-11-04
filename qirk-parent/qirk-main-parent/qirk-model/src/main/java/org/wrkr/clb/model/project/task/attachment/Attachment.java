package org.wrkr.clb.model.project.task.attachment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.project.task.Task;

@Entity
@Table(name = AttachmentMeta.TABLE_NAME)
public class Attachment extends BaseIdEntity {

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = AttachmentMeta.externalPath, nullable = false)
    private String externalPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
    @Transient
    private Long taskId;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getExternalPath() {
        return externalPath;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Long getTaskId() {
        if (taskId == null) {
            return (task == null ? null : task.getId());
        }
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
