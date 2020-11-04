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
