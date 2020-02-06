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
package org.wrkr.clb.services.file;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.DropboxSettings;
import org.wrkr.clb.model.project.Project;


@Validated
public interface TemporaryAttachmentService {

    public String create(@NotNull(message = "externalPath create must not be null") String externalPath,
            @NotNull(message = "task create must not be null") Project project,
            @NotNull(message = "dropboxSettings create must not be null") DropboxSettings dropboxSettings);

    public String create(@NotNull(message = "externalPath create must not be null") String externalPath,
            @NotNull(message = "task create must not be null") Project project);

    public void clearTemporaryAttachments();
}
