/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
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
package org.wrkr.clb.model.organization;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wrkr.clb.model.BaseVersionedEntity;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.User;

@Entity
@Table(name = OrganizationMemberMeta.TABLE_NAME)
public class OrganizationMember extends BaseVersionedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Transient
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Transient
    private Long organizationId;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;

    @Column(name = "manager", nullable = false)
    private boolean manager = false;

    @Column(name = "fired", nullable = false)
    private boolean fired = false;

    @OneToMany(mappedBy = "organizationMember", fetch = FetchType.LAZY)
    private List<ProjectMember> projectMembership = new ArrayList<ProjectMember>();

    @OneToMany(mappedBy = "reporter", fetch = FetchType.LAZY)
    private List<Task> reportedTasks = new ArrayList<Task>();

    @OneToMany(mappedBy = "assignee", fetch = FetchType.LAZY)
    private List<Task> assignedTasks = new ArrayList<Task>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Memo> memos = new ArrayList<Memo>();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrganizationId() {
        if (organizationId == null) {
            return (organization == null ? null : organization.getId());
        }
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public boolean isFired() {
        return fired;
    }

    public void setFired(boolean fired) {
        this.fired = fired;
    }

    public List<ProjectMember> getProjectMembership() {
        return projectMembership;
    }

    public void setProjectMembership(List<ProjectMember> projectMembership) {
        this.projectMembership = projectMembership;
    }

    public List<Task> getReportedTasks() {
        return reportedTasks;
    }

    public void setReportedTasks(List<Task> reportedTasks) {
        this.reportedTasks = reportedTasks;
    }

    public List<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(List<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public List<Memo> getMemos() {
        return memos;
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
    }

}
