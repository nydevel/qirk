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
