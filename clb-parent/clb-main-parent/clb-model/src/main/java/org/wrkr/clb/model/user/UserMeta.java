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

import org.wrkr.clb.model.BaseIdEntityMeta;

public class UserMeta extends BaseIdEntityMeta {

    public static final String TABLE_NAME = "user_profile";

    public static final String username = "username";
    public static final String emailAddress = "email_address";
    public static final String passwordHash = "password_hash";
    @Deprecated
    public static final String enabled = "enabled";
    public static final String licenseAccepted = "license_accepted";
    public static final String createdAt = "created_at";
    public static final String fullName = "full_name";
    public static final String dontRecommend = "dont_recommend";
    public static final String about = "about";

    public static final UserToTagMeta tags = new UserToTagMeta();
    public static final UserToLanguageMeta languages = new UserToLanguageMeta();
}
