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
package org.wrkr.clb.services.api.dropbox.impl;

import java.io.File;
import java.util.List;

import org.wrkr.clb.services.api.dropbox.DropboxApiService;


@Deprecated
@SuppressWarnings("unused")
public class DropboxApiServiceStub implements DropboxApiService {

    @Override
    public String getToken(String code, String redirectURI) throws Exception {
        return null;
    }

    @Override
    public String getRedirectURIForOrganization() {
        return null;
    }

    @Override
    public String getRedirectURIForProject() {
        return null;
    }

    @Override
    public String upload(String token, String path, File file) {
        return path;
    }

    @Override
    public String getTemporaryLink(String token, String path) {
        return null;
    }

    @Override
    public String getThumbnail(String token, String path) {
        return null;
    }

    @Override
    public List<byte[]> getThumbnailBatch(String token, List<String> pathList) {
        return null;
    }

    @Override
    public void delete(String token, String path) {
    }
}
