package org.wrkr.clb.model.user;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.wrkr.clb.model.BaseIdEntity;
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
