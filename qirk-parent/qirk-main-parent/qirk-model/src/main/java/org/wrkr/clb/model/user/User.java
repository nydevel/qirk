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
package org.wrkr.clb.model.user;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wrkr.clb.model.BaseIdEntity;
import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.Tag;
import org.wrkr.clb.model.project.Issue;
import org.wrkr.clb.model.project.ProjectMember;

@Entity
@Table(name = UserMeta.TABLE_NAME)
public class User extends BaseIdEntity {

    // Authentication

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email_address", nullable = false, unique = true)
    private String emailAddress;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime createdAt;

    // Authorization

    @Column(name = "manager", nullable = false)
    private boolean manager = false;

    // Profile

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Transient
    private NotificationSettings notificationSettings;
    @Transient
    private Boolean sendEmailNotifications;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_tag", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
            @JoinColumn(name = "tag_id") })
    private List<Tag> tags = new ArrayList<Tag>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_language", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = {
            @JoinColumn(name = "language_id") })
    private List<Language> languages = new ArrayList<Language>();

    // Projects

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ProjectMember> projectMembership = new ArrayList<ProjectMember>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserFavorite> favorites = new ArrayList<UserFavorite>();

    @OneToMany(mappedBy = "reporter", fetch = FetchType.LAZY)
    private List<Issue> issues = new ArrayList<Issue>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public Boolean getSendEmailNotifications() {
        return sendEmailNotifications;
    }

    public void setSendEmailNotifications(Boolean sendEmailNotifications) {
        this.sendEmailNotifications = sendEmailNotifications;
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

    public List<ProjectMember> getProjectMembership() {
        return projectMembership;
    }

    public void setProjectMembership(List<ProjectMember> projectMembership) {
        this.projectMembership = projectMembership;
    }

    public List<UserFavorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<UserFavorite> favorites) {
        this.favorites = favorites;
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    @Override
    public String toString() {
        return "User [id=" + getId() + "]";
    }

    public static Set<Long> toIds(List<User> users) {
        Set<Long> ids = new HashSet<Long>(users.size());
        for (User user : users) {
            ids.add(user.getId());
        }
        return ids;
    }

    public static Set<String> toEmailsIfSendEmailNotifications(List<User> users) {
        Set<String> emails = new HashSet<String>(users.size());
        for (User user : users) {
            if (user.getSendEmailNotifications()) {
                emails.add(user.getEmailAddress());
            }
        }
        return emails;
    }
}
