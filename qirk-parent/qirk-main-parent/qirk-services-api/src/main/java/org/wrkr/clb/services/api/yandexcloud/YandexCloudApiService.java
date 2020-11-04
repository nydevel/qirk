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
