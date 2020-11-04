package org.wrkr.clb.model.project.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.project.Project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = TaskHashtagMeta.TABLE_NAME)
public class TaskHashtag extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;
    @Transient
    @JsonIgnore
    private Long projectId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "hashtags", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Task> tasks = new ArrayList<Task>();
    @Transient
    @JsonProperty(value = "tasks_count")
    @JsonInclude(Include.NON_NULL)
    private Long tasksCount;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Long getTasksCount() {
        return tasksCount;
    }

    public void setTasksCount(Long tasksCount) {
        this.tasksCount = tasksCount;
    }

    public static TaskHashtag fromMap(Map<String, Object> map, Long projectId) {
        TaskHashtag hashtag = new TaskHashtag();

        hashtag.setId((Long) map.get(TaskHashtagMeta.id));
        hashtag.setName((String) map.get(TaskHashtagMeta.name));
        hashtag.setProjectId(projectId);

        return hashtag;
    }
}
