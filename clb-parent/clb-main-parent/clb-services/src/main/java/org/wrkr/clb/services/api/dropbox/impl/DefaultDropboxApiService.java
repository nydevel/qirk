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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.wrkr.clb.common.util.collections.MapBuilder;
import org.wrkr.clb.common.util.io.DeleteOnCloseFileInputStream;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.services.api.dropbox.DropboxApiService;
import org.wrkr.clb.services.api.impl.DefaultOAuthService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.NotFoundException;


//@Service configured in clb-services-ctx.xml
@Validated
public class DefaultDropboxApiService extends DefaultOAuthService implements DropboxApiService {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDropboxApiService.class);

    protected final String OAUTH_TOKEN_URL = "https://api.dropboxapi.com/oauth2/token";

    @Override
    protected String getOauthTokenURL() {
        return OAUTH_TOKEN_URL;
    }

    private static final String UPLOAD_URL = "https://content.dropboxapi.com/2/files/upload";
    private static final String GET_TEMPORARY_LINK_URL = "https://api.dropboxapi.com/2/files/get_temporary_link";
    private static final String GET_THUMBNAIL_URL = "https://content.dropboxapi.com/2/files/get_thumbnail";
    private static final String GET_THUMBNAIL_BATCH_URL = "https://content.dropboxapi.com/2/files/get_thumbnail_batch";
    private static final String DELETE_URL = "https://api.dropboxapi.com/2/files/delete_v2";

    private static final String DROPBOX_API_ARG_HEADER = "Dropbox-API-Arg";

    private static final String THUMBNAIL_SIZE_ARG = "w128h128";

    private static class JsonStatusCode extends org.wrkr.clb.services.util.http.JsonStatusCode {
        // 400
        public static final String UNSUPPORTED_EXTENSION = "UNSUPPORTED_EXTENSION";
        public static final String UNSUPPORTED_IMAGE = "UNSUPPORTED_IMAGE";
    }

    // credentials config values
    protected String clientId;
    protected String clientSercet;

    // redirect uri config values
    private String redirectURIForOrganization;
    private String redirectURIForProject;

    @Override
    protected String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    protected String getClientSecret() {
        return clientSercet;
    }

    public void setClientSercet(String clientSercet) {
        this.clientSercet = clientSercet;
    }

    @Override
    public String getRedirectURIForOrganization() {
        return redirectURIForOrganization;
    }

    public void setRedirectURIForOrganization(String redirectURIForOrganization) {
        this.redirectURIForOrganization = redirectURIForOrganization;
    }

    @Override
    public String getRedirectURIForProject() {
        return redirectURIForProject;
    }

    public void setRedirectURIForProject(String redirectURIForProject) {
        this.redirectURIForProject = redirectURIForProject;
    }

    @SuppressWarnings("rawtypes")
    private ResponseEntity<Map> postUploadRequest(File file, HttpHeaders headers) throws IOException {
        try (DeleteOnCloseFileInputStream stream = new DeleteOnCloseFileInputStream(file)) {
            InputStreamResource resource = new InputStreamResource(stream);
            HttpEntity<InputStreamResource> request = new HttpEntity<InputStreamResource>(resource, headers);

            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.postForEntity(UPLOAD_URL, request, Map.class);
        }
    }

    @Override
    public String upload(String token, String path, File file) throws Exception {
        token = token.strip();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        Map<String, Object> args = new MapBuilder<String, Object>()
                .put("path", path)
                .put("mode", "add")
                .put("autorename", true)
                .put("mute", true)
                .put("strict_conflict", true).build();
        headers.set(DROPBOX_API_ARG_HEADER, JsonUtils.convertMapToJson(args));

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = postUploadRequest(file, headers);

        @SuppressWarnings("unchecked")
        Map<String, String> responseMap = response.getBody();
        return responseMap.get("path_display");
    }

    @Override
    public String getTemporaryLink(String token, String path) {
        token = token.strip();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("path", path);
        HttpEntity<Map<String, String>> request = new HttpEntity<Map<String, String>>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(GET_TEMPORARY_LINK_URL, request, Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = response.getBody();
        return (String) responseMap.get("link");
    }

    @Override
    public String getThumbnail(String token, String path) throws Exception {
        token = token.strip();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.TEXT_PLAIN);

        Map<String, String> args = new MapBuilder<String, String>()
                .put("path", path)
                .put("size", THUMBNAIL_SIZE_ARG)
                .put("mode", "strict").build();
        headers.set(DROPBOX_API_ARG_HEADER, JsonUtils.convertMapToJson(args));

        HttpEntity<Void> request = new HttpEntity<Void>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(GET_THUMBNAIL_URL, request, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            Map<String, Object> response = JsonUtils.<Object>convertJsonToMap(e.getResponseBodyAsString());

            String error = (String) response.get("error_summary");
            if (error != null) {
                if (error.startsWith("path/")) {
                    throw new NotFoundException("File");
                }
                if (error.startsWith("unsupported_extension/")) {
                    throw new BadRequestException(JsonStatusCode.UNSUPPORTED_EXTENSION, "Unsupported extension.", e);
                }
                if (error.startsWith("unsupported_image/")) {
                    throw new BadRequestException(JsonStatusCode.UNSUPPORTED_IMAGE, "Unsupported image.", e);
                }
            }

            throw new ApplicationException("A server error occurred during getting thumbnail from dropbox.", e);
        }
    }

    @Override
    public List<byte[]> getThumbnailBatch(String token, List<String> pathList) throws IOException {
        token = token.strip();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ArrayList<Map<String, String>> entries = new ArrayList<Map<String, String>>();
        for (String path : pathList) {
            Map<String, String> entry = new MapBuilder<String, String>()
                    .put("path", path)
                    .put("size", THUMBNAIL_SIZE_ARG)
                    .put("mode", "strict").build();
            entries.add(entry);
        }

        Map<String, Object> requestBody = new HashMap<String, Object>();
        requestBody.put("entries", entries);
        HttpEntity<Map<String, Object>> request = new HttpEntity<Map<String, Object>>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(GET_THUMBNAIL_BATCH_URL, request, String.class);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> responseEntries = (List<Map<String, Object>>) JsonUtils.convertJsonToMap(response.getBody())
                .get("entries");
        List<byte[]> thumbnails = new ArrayList<byte[]>();
        for (Map<String, Object> entry : responseEntries) {
            thumbnails.add(Base64.getDecoder().decode((String) entry.get("thumbnail")));
        }
        return thumbnails;
    }

    @Override
    public void delete(String token, String path) {
        token = token.strip();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("path", path);
        HttpEntity<Map<String, String>> request = new HttpEntity<Map<String, String>>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(DELETE_URL, request, String.class);
    }
}
