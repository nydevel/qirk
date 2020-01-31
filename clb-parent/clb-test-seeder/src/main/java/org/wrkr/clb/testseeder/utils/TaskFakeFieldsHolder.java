package org.wrkr.clb.testseeder.utils;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.task.TaskPriority;
import org.wrkr.clb.model.task.TaskStatus;
import org.wrkr.clb.model.task.TaskType;

/**
 * @author Denis Bilenko
 */
public class TaskFakeFieldsHolder {

    private static TaskFakeFieldsHolder instance = new TaskFakeFieldsHolder();

    private List<TaskPriority> prios = new ArrayList<TaskPriority>();
    private List<TaskStatus> statuses = new ArrayList<TaskStatus>();
    private List<TaskType> types = new ArrayList<TaskType>();

    private TaskFakeFieldsHolder() {
    }

    public static TaskFakeFieldsHolder getInstance() {
        return instance;
    }

    public List<TaskPriority> getPrios() {
        return prios;
    }

    public void setPrios(List<TaskPriority> prios) {
        this.prios = prios;
    }

    public List<TaskStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<TaskStatus> statuses) {
        this.statuses = statuses;
    }

    public List<TaskType> getTypes() {
        return types;
    }

    public void setTypes(List<TaskType> types) {
        this.types = types;
    }

}
