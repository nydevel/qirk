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
package org.wrkr.clb.model.user;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.project.Project;


@Entity
@Table(name = UserFavoriteMeta.TABLE_NAME)
public class UserFavorite extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Transient
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = true)
    private Project project;
    @Transient
    private Long projectId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_id", nullable = true)
    private UserFavorite previous;
    @Transient
    private Long previousId;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "previous")
    private UserFavorite next;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public UserFavorite getPrevious() {
        return previous;
    }

    public void setPrevious(UserFavorite previous) {
        this.previous = previous;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getPreviousId() {
        return previousId;
    }

    public void setPreviousId(Long previousId) {
        this.previousId = previousId;
    }

    public UserFavorite getNext() {
        return next;
    }

    public void setNext(UserFavorite next) {
        this.next = next;
    }
}
