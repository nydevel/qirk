package org.wrkr.clb.model.project.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.wrkr.clb.model.BaseIdEntity;


@Entity
@Table(name = ProjectTaskNumberSequenceMeta.TABLE_NAME)
public class ProjectTaskNumberSequence extends BaseIdEntity {

    @Column(name = "next_task_number", nullable = false)
    private Long nextTaskNumber = 1L;

    public Long getNextTaskNumber() {
        return nextTaskNumber;
    }

    public void setNextTaskNumber(Long nextTaskNumber) {
        this.nextTaskNumber = nextTaskNumber;
    }
}
