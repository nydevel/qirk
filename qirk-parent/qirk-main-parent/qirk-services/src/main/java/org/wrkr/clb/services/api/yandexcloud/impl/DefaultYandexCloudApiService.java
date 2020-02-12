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
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.wrkr.clb.services.api.yandexcloud.YandexCloudApiService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

//@Service configured in clb-services-ctx.xml
public class DefaultYandexCloudApiService implements YandexCloudApiService {

    private static final int TEMPORARY_LINK_EXPIRATION_HOURS = 1;

    private static class JsonStatusCode extends org.wrkr.clb.services.util.http.JsonStatusCode {
        // 400
        public static final String FILE_NOT_FOUND = "FILE_NOT_FOUND";
    }

    @Deprecated
    private String bucketName = "";

    @Autowired
    private AmazonS3 client;

    @Override
    public void upload(String path, File file) {
        client.putObject(bucketName, path, file);
    }

    @Override
    public void upload(String path, InputStream input) {
        client.putObject(bucketName, path, input, new ObjectMetadata());
    }

    @Override
    public void upload(String path, InputStream input, Map<String, String> userMetadata) {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setUserMetadata(userMetadata);
        client.putObject(bucketName, path, input, meta);
    }

    @Override
    public void copy(String sourcePath, String destinationPath) {
        client.copyObject(bucketName, sourcePath, bucketName, destinationPath);
    }

    @Override
    public boolean hasFile(String path) throws Exception {
        try (S3Object object = client.getObject(bucketName, path)) {
        } catch (AmazonS3Exception e) {
            if (ErrorCode.NO_SUCH_KEY.equals(e.getErrorCode())) {
                return false;
            }
            throw new ApplicationException("A server error occurred during getting file from to yandex cloud.", e);
        }
        return true;
    }

    @Override
    public String getTemporaryLink(String path) {
        URL presignedURL = client.generatePresignedUrl(
                bucketName, path,
                DateTime.now().plusHours(TEMPORARY_LINK_EXPIRATION_HOURS).toDate(),
                HttpMethod.GET);
        return presignedURL.toExternalForm();
    }

    @Override
    public S3ObjectInputStream getFile(String path) {
        return client.getObject(bucketName, path).getObjectContent();
    }

    @Override
    public S3ObjectInputStream getFileOrThrowApplicationException(String path,
            String notFoundErrorCode, String notFoundErrorMessage) throws ApplicationException {
        try {
            return getFile(path);
        } catch (AmazonS3Exception e) {
            if (ErrorCode.NO_SUCH_KEY.equals(e.getErrorCode())) {
                throw new BadRequestException(notFoundErrorCode, notFoundErrorMessage, e);
            }
            throw new ApplicationException("A server error occurred during getting file from to yandex cloud.", e);
        }
    }

    @Override
    public S3ObjectInputStream getFileOrThrowApplicationException(String path) throws ApplicationException {
        return getFileOrThrowApplicationException(path, JsonStatusCode.FILE_NOT_FOUND, "There was no file at specified path.");
    }

    @Override
    public List<String> listFolders(String path, char delimeter) {
        ListObjectsRequest request = new ListObjectsRequest(bucketName, path, null, String.valueOf(delimeter), null);
        return client.listObjects(request).getCommonPrefixes();
    }

    @Override
    public List<String> listFolders(String path) {
        return listFolders(path, '/');
    }

    @Override
    public void deleteFile(String path) {
        client.deleteObject(bucketName, path);
    }

    @Override
    public void deleteFiles(String... paths) {
        DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName).withKeys(paths);
        client.deleteObjects(request);
    }
}
