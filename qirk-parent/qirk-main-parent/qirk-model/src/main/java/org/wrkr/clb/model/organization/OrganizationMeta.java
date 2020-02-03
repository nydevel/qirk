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

import org.wrkr.clb.model.BaseUiIdEntityMeta;

public class OrganizationMeta extends BaseUiIdEntityMeta {

    public static final String TABLE_NAME = "organization";

    public static final String name = "name";
    public static final String isPrivate = "private";
    public static final String ownerId = "owner_user_id";
    public static final String predefinedForUser = "predefined_for_user";
    public static final String frozen = "frozen";
    public static final String dropboxSettingsId = "dropbox_settings_id";

    public static final OrganizationToLanguageMeta languages = new OrganizationToLanguageMeta();
}
