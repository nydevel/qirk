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
package org.wrkr.clb.services.api.yandexcloud.impl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wrkr.clb.services.api.yandexcloud.YandexCloudApiService;


@SuppressWarnings("unused")
public class YandexCloudApiServiceStub implements YandexCloudApiService {

    @Override
    public void upload(String path, File file) {
    }

    @Override
    public void upload(String path, InputStream input) {
    }

    @Override
    public void upload(String path, InputStream input, Map<String, String> userMetadata) {
    }

    @Override
    public void copy(String sourcePath, String destinationPath) {
    }

    @Override
    public boolean hasFile(String path) throws Exception {
        return true;
    }

    @Override
    public String getTemporaryLink(String path) {
        return "";
    }

    @Override
    public InputStream getFile(String path) {
        return null;
    }

    @Override
    public InputStream getFileOrThrowApplicationException(String path, String notFoundErrorCode, String notFoundErrorMessage)
            throws Exception {
        return null;
    }

    @Override
    public InputStream getFileOrThrowApplicationException(String path) {
        return null;
    }

    @Override
    public List<String> listFolders(String path, char delimeter) {
        return new ArrayList<String>();
    }

    @Override
    public List<String> listFolders(String path) {
        return new ArrayList<String>();
    }

    @Override
    public void deleteFile(String path) {
    }

    @Override
    public void deleteFiles(String... paths) {
    }
}
