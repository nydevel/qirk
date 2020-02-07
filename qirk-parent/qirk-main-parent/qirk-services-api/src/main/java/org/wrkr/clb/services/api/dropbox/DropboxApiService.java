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
package org.wrkr.clb.services.api.dropbox;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.services.api.OAuthService;

@Validated
public interface DropboxApiService extends OAuthService {

    @Deprecated
    public String getRedirectURIForProject();

    @Deprecated
    public String upload(@NotNull(message = "token must not be null") String token,
            @NotNull(message = "path must not be null") String path,
            @NotNull(message = "file must not be null") File file) throws Exception;

    public String getTemporaryLink(@NotNull(message = "token must not be null") String token,
            @NotNull(message = "path must not be null") String path) throws Exception;

    @Deprecated
    public String getThumbnail(
            @NotNull(message = "token must not be null") String token,
            @NotNull(message = "path must not be null") String path) throws Exception;

    @Deprecated
    public List<byte[]> getThumbnailBatch(
            @NotNull(message = "token must not be null") String token,
            @NotNull(message = "pathList must not be null") List<String> pathList) throws Exception;

    public void delete(@NotNull(message = "token must not be null") String token,
            @NotNull(message = "path must not be null") String path) throws Exception;
}
