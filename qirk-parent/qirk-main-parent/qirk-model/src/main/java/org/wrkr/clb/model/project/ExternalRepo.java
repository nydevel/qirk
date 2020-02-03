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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.wrkr.clb.model.BaseIdEntity;


@Entity
@Table(name = "external_repo")
public class ExternalRepo extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private ExternalRepoType type;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "repo_owner_name", nullable = false)
    private String repoOwnerName;

    @Column(name = "repo_name", nullable = false)
    private String repoName;

    @Column(name = "repo_url", nullable = false)
    private String repoURL;

    public ExternalRepoType getType() {
        return type;
    }

    public void setType(ExternalRepoType type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRepoOwnerName() {
        return repoOwnerName;
    }

    public void setRepoOwnerName(String repoOwnerName) {
        this.repoOwnerName = repoOwnerName;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoURL() {
        return repoURL;
    }

    public void setRepoURL(String repoURL) {
        this.repoURL = repoURL;
    }
}
