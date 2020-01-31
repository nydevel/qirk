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
package org.wrkr.clb.services.api.yandexcloud;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface YandexCloudApiService {

    public static class ErrorCode {
        public static final String NO_SUCH_KEY = "NoSuchKey";
    }

    public void upload(String path, File file);

    public void upload(String path, InputStream input);

    public void upload(String path, InputStream input, Map<String, String> userMetadata);

    public void copy(String sourcePath, String destinationPath);

    public boolean hasFile(String path) throws Exception;

    public String getTemporaryLink(String path);

    public InputStream getFile(String path);

    public InputStream getFileOrThrowApplicationException(String path, String notFoundErrorCode, String notFoundErrorMessage)
            throws Exception;

    public InputStream getFileOrThrowApplicationException(String path) throws Exception;

    public List<String> listFolders(String path, char delimeter);

    public List<String> listFolders(String path);

    public void deleteFile(String path);

    public void deleteFiles(String... paths);
}
