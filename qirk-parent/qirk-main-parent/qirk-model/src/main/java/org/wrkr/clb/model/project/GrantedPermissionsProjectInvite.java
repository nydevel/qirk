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

import org.wrkr.clb.model.user.User;


@Entity
@Table(name = GrantedPermissionsProjectInviteMeta.TABLE_NAME)
public class GrantedPermissionsProjectInvite extends BaseProjectInvite {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "write_allowed", nullable = false)
    private boolean writeAllowed = false;

    @Column(name = "manager", nullable = false)
    private boolean manager = false;

    // OneToOne is not used because hibernate fetches reverse OneToOnes eagerly
    @OneToMany(mappedBy = "invite", fetch = FetchType.LAZY)
    private List<ProjectInviteToken> tokens = new ArrayList<ProjectInviteToken>();

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
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

    public ProjectInviteToken getToken() {
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.get(0);
    }

    public void setToken(ProjectInviteToken token) {
        this.tokens = Arrays.asList(token);
    }
}
