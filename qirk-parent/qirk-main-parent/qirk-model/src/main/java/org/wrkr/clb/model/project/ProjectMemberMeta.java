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

import org.wrkr.clb.model.BaseIdEntityMeta;

public class ProjectMemberMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "project_member";

    public static final String userId = "user_id";
    public static final String projectId = "project_id";
    public static final String writeAllowed = "write_allowed";
    public static final String manager = "manager";
    public static final String hiredAt = "hired_at";
    public static final String firedAt = "fired_at";
    public static final String fired = "fired";

    public static final ProjectMemberMeta DEFAULT = new ProjectMemberMeta(TABLE_NAME);

    public ProjectMemberMeta(String tableAlias) {
        super(tableAlias);
    }
}
