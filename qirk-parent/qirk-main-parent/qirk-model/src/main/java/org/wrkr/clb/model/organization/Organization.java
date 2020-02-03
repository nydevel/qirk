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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wrkr.clb.model.BaseUiIdEntity;
import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.user.User;

@Entity
@Table(name = OrganizationMeta.TABLE_NAME)
public class Organization extends BaseUiIdEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "private", nullable = false)
    private boolean isPrivate = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private User owner;
    @Transient
    private Long ownerId;

    @Column(name = "predefined_for_user")
    private boolean predefinedForUser = false;

    @Deprecated
    @Column(name = "frozen", nullable = false)
    private boolean frozen = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropbox_settings_id", nullable = true)
    private DropboxSettings dropboxSettings;
    @Transient
    private Long dropboxSettingsId;

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<OrganizationMember> members = new ArrayList<OrganizationMember>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "organization_language", joinColumns = { @JoinColumn(name = "organization_id") }, inverseJoinColumns = {
            @JoinColumn(name = "language_id") })
    private List<Language> languages = new ArrayList<Language>();

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<Project>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Long getOwnerId() {
        if (ownerId == null) {
            return (owner == null ? null : owner.getId());
        }
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public boolean getPredefinedForUser() {
        return predefinedForUser;
    }

    public void setPredefinedForUser(boolean predefinedForUser) {
        this.predefinedForUser = predefinedForUser;
    }

    @Deprecated
    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public DropboxSettings getDropboxSettings() {
        return dropboxSettings;
    }

    public void setDropboxSettings(DropboxSettings dropboxSettings) {
        this.dropboxSettings = dropboxSettings;
    }

    public Long getDropboxSettingsId() {
        return dropboxSettingsId;
    }

    public void setDropboxSettingsId(Long dropboxSettingsId) {
        this.dropboxSettingsId = dropboxSettingsId;
    }

    public List<OrganizationMember> getMembers() {
        return members;
    }

    public void setMembers(List<OrganizationMember> members) {
        this.members = members;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @Override
    public String toString() {
        return "Organization [id=" + getId() + "; recordVersion=" + getRecordVersion() + "]";
    }
}
