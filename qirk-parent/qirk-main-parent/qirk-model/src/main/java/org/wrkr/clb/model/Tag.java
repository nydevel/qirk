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
package org.wrkr.clb.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.user.User;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = TagMeta.TABLE_NAME)
public class Tag extends BaseIdEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users = new ArrayList<User>();

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Project> projects = new ArrayList<Project>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static Tag fromMap(Map<String, Object> map) {
        Tag tag = new Tag();

        tag.setId((Long) map.get(TagMeta.id));
        tag.setName((String) map.get(TagMeta.name));

        return tag;
    }
}
