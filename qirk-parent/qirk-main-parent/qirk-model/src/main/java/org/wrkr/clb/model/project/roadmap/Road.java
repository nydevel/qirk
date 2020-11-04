package org.wrkr.clb.model.project.roadmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.BaseVersionedEntityMeta;
import org.wrkr.clb.model.VersionedIdEntity;
import org.wrkr.clb.model.project.Project;

@Entity
@Table(name = RoadMeta.TABLE_NAME)
public class Road extends BaseIdEntity implements VersionedIdEntity {

    @Column(name = BaseVersionedEntityMeta.recordVersion, nullable = false)
    private Long recordVersion = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    @Transient
    private Long projectId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_id", nullable = true)
    private Road previous;
    @Transient
    private Long previousId;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    // OneToOne is not used because hibernate fetches reverse OneToOnes eagerly
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "previous")
    private List<Road> nextRoads;
    @Transient
    private Long nextId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "road")
    private List<TaskCard> cards = new ArrayList<TaskCard>();

    @Override
    public Long getRecordVersion() {
        return recordVersion;
    }

    @Override
    public void setRecordVersion(Long recordVersion) {
        this.recordVersion = recordVersion;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.projectId = project.getId();
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
        this.project = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Road getPrevious() {
        return previous;
    }

    public void setPrevious(Road previous) {
        this.previous = previous;
        this.previousId = (previous == null ? null : previous.getId());
    }

    public Long getPreviousId() {
        if (previousId == null) {
            return (previous == null ? null : previous.getId());
        }
        return previousId;
    }

    public void setPreviousId(Long previousId) {
        this.previousId = previousId;
        this.previous = null;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Road getNext() {
        if (nextRoads.isEmpty()) {
            return null;
        }
        return nextRoads.get(0);
    }

    public void setNext(Road next) {
        this.nextRoads = Arrays.asList(next);
    }

    public List<Road> getNextRoads() {
        return nextRoads;
    }

    public void setNextRoads(List<Road> nextRoads) {
        this.nextRoads = nextRoads;
    }

    public Long getNextId() {
        return nextId;
    }

    public void setNextId(Long nextId) {
        this.nextId = nextId;
    }

    public List<TaskCard> getCards() {
        return cards;
    }

    public void setCards(List<TaskCard> cards) {
        this.cards = cards;
    }
}
