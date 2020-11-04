package org.wrkr.clb.model.project.task;

import org.wrkr.clb.model.BaseEntity;

public class TaskLink extends BaseEntity {

    private Long task1Id;
    private Long task2Id;

    public Long getTask1Id() {
        return task1Id;
    }

    public void setTask1Id(Long task1Id) {
        this.task1Id = task1Id;
    }

    public Long getTask2Id() {
        return task2Id;
    }

    public void setTask2Id(Long task2Id) {
        this.task2Id = task2Id;
    }
}
