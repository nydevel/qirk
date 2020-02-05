/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.model.project;

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
import org.wrkr.clb.model.VersionedEntity;
import org.wrkr.clb.model.project.task.TaskCard;

@Entity
@Table(name = RoadMeta.TABLE_NAME)
public class Road extends BaseIdEntity implements VersionedEntity {

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
