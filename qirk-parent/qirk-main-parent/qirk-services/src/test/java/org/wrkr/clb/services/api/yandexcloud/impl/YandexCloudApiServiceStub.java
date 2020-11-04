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
