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
package org.wrkr.clb.model.project;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = ProjectMemberMeta.TABLE_NAME)
public class ProjectMember extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
    @Transient
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_organization_id", nullable = false)
    @JsonIgnore
    private OrganizationMember organizationMember;
    @Transient
    private Long organizationMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;
    @Transient
    private Long projectId;

    @Column(name = "write_allowed", nullable = false)
    private boolean writeAllowed = false;

    @Column(name = "manager", nullable = false)
    private boolean manager = false;

    @Column(name = "hired_at", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime hiredAt;

    @Column(name = "fired_at", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = true)
    private OffsetDateTime firedAt;

    @Column(name = "fired", nullable = false)
    private boolean fired = false;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrganizationMember getOrganizationMember() {
        return organizationMember;
    }

    public void setOrganizationMember(OrganizationMember organizationMember) {
        this.organizationMember = organizationMember;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getUserId() {
        if (userId == null) {
            return (user == null ? null : user.getId());
        }
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrganizationMemberId() {
        return organizationMemberId;
    }

    public void setOrganizationMemberId(Long organizationMemberId) {
        this.organizationMemberId = organizationMemberId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public boolean isWriteAllowed() {
        return writeAllowed;
    }

    public void setWriteAllowed(boolean writeAllowed) {
        this.writeAllowed = writeAllowed;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public OffsetDateTime getHiredAt() {
        return hiredAt;
    }

    public void setHiredAt(OffsetDateTime hiredAt) {
        this.hiredAt = hiredAt;
    }

    public OffsetDateTime getFiredAt() {
        return firedAt;
    }

    public void setFiredAt(OffsetDateTime firedAt) {
        this.firedAt = firedAt;
    }

    public boolean isFired() {
        return fired;
    }

    public void setFired(boolean fired) {
        this.fired = fired;
    }
}
