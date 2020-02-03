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
import org.wrkr.clb.model.Tag;
import org.wrkr.clb.model.organization.DropboxSettings;
import org.wrkr.clb.model.organization.Organization;
import org.wrkr.clb.model.organization.OrganizationMember;
import org.wrkr.clb.model.project.task.ProjectTaskNumberSequence;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.model.user.UserFavorite;

@Entity
@Table(name = ProjectMeta.TABLE_NAME)
public class Project extends BaseUiIdEntity {

    public static final long MIN_PRIVATE_PROJECT_MEMBERS_TO_CHARGE = 6L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    @Transient
    private Long organizationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_number_sequence_id", nullable = false)
    private ProjectTaskNumberSequence taskNumberSequence;
    @Transient
    private Long taskNumberSequenceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "private", nullable = false)
    private boolean isPrivate = false;

    @Column(name = "description_md", nullable = false)
    private String descriptionMd;

    @Column(name = "description_html", nullable = false)
    private String descriptionHtml;

    @Column(name = "documentation_md", nullable = false)
    private String documentationMd;

    @Column(name = "documentation_html", nullable = false)
    private String documentationHtml;

    @Deprecated
    @Column(name = "frozen", nullable = false)
    private boolean frozen = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropbox_settings_id", nullable = true)
    private DropboxSettings dropboxSettings;
    @Transient
    private Long dropboxSettingsId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_repo_id", nullable = true)
    private ExternalRepo externalRepo;
    @Transient
    private Long externalRepoId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_tag", joinColumns = { @JoinColumn(name = "project_id") }, inverseJoinColumns = {
            @JoinColumn(name = "tag_id") })
    private List<Tag> tags = new ArrayList<Tag>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "project_language", joinColumns = { @JoinColumn(name = "project_id") }, inverseJoinColumns = {
            @JoinColumn(name = "language_id") })
    private List<Language> languages = new ArrayList<Language>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectMember> members = new ArrayList<ProjectMember>();

    @Transient
    private List<OrganizationMember> organizationMembers = new ArrayList<OrganizationMember>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<Task>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<UserFavorite> favorites = new ArrayList<UserFavorite>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectInvite> invites = new ArrayList<ProjectInvite>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<GrantedPermissionsProjectInvite> grantedPermissionsInvites = new ArrayList<GrantedPermissionsProjectInvite>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<ProjectApplication> applications = new ArrayList<ProjectApplication>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Memo> memos = new ArrayList<Memo>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Issue> issues = new ArrayList<Issue>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescriptionMd() {
        return descriptionMd;
    }

    public void setDescriptionMd(String descriptionMd) {
        this.descriptionMd = descriptionMd;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public String getDocumentationMd() {
        return documentationMd;
    }

    public void setDocumentationMd(String documentationMd) {
        this.documentationMd = documentationMd;
    }

    public String getDocumentationHtml() {
        return documentationHtml;
    }

    public void setDocumentationHtml(String documentationHtml) {
        this.documentationHtml = documentationHtml;
    }

    @Deprecated
    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public DropboxSettings getDropboxSettings() {
        return dropboxSettings;
    }

    public void setDropboxSettings(DropboxSettings dropboxSettings) {
        this.dropboxSettings = dropboxSettings;
    }

    public ExternalRepo getExternalRepo() {
        return externalRepo;
    }

    public void setExternalRepo(ExternalRepo externalRepo) {
        this.externalRepo = externalRepo;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getDropboxSettingsId() {
        return dropboxSettingsId;
    }

    public void setDropboxSettingsId(Long dropboxSettingsId) {
        this.dropboxSettingsId = dropboxSettingsId;
    }

    public Long getTaskNumberSequenceId() {
        return taskNumberSequenceId;
    }

    public void setTaskNumberSequenceId(Long taskNumberSequenceId) {
        this.taskNumberSequenceId = taskNumberSequenceId;
    }

    public Long getExternalRepoId() {
        return externalRepoId;
    }

    public void setExternalRepoId(Long externalRepoId) {
        this.externalRepoId = externalRepoId;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }

    public void setMembers(List<ProjectMember> members) {
        this.members = members;
    }

    public List<OrganizationMember> getOrganizationMembers() {
        return organizationMembers;
    }

    public void setOrganizationMembers(List<OrganizationMember> organizationMembers) {
        this.organizationMembers = organizationMembers;
    }

    public ProjectTaskNumberSequence getTaskNumberSequence() {
        return taskNumberSequence;
    }

    public void setTaskNumberSequence(ProjectTaskNumberSequence taskNumberSequence) {
        this.taskNumberSequence = taskNumberSequence;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<UserFavorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<UserFavorite> favorites) {
        this.favorites = favorites;
    }

    public List<ProjectInvite> getInvites() {
        return invites;
    }

    public void setInvites(List<ProjectInvite> invites) {
        this.invites = invites;
    }

    public List<GrantedPermissionsProjectInvite> getGrantedPermissionsInvites() {
        return grantedPermissionsInvites;
    }

    public void setGrantedPermissionsInvites(List<GrantedPermissionsProjectInvite> grantedPermissionsInvites) {
        this.grantedPermissionsInvites = grantedPermissionsInvites;
    }

    public List<ProjectApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<ProjectApplication> applications) {
        this.applications = applications;
    }

    public List<Memo> getMemos() {
        return memos;
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}
