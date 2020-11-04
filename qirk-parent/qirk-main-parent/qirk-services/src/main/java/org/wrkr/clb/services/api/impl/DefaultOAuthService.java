package org.wrkr.clb.services.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.wrkr.clb.services.api.OAuthService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class DefaultOAuthService implements OAuthService {

    protected abstract String getOauthTokenURL();

    // credentials uri config values
    protected abstract String getClientId();

    protected abstract String getClientSecret();

    protected static final String GRANT_TYPE = "authorization_code";

    @Override
    public String getToken(String code, String redirectURI) throws JsonParseException, JsonMappingException, IOException {
        code = code.strip();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);

        List<String> parameters = new ArrayList<String>();
        parameters.add("code=" + code);
        parameters.add("client_id=" + getClientId());
        parameters.add("client_secret=" + getClientSecret());
        parameters.add("redirect_uri=" + redirectURI);
        parameters.add("grant_type=" + GRANT_TYPE);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate
                .postForEntity(getOauthTokenURL() + "?" + String.join("&", parameters), request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> responseMap = mapper.readValue(response.getBody(), new TypeReference<Map<String, String>>() {
        });
        return responseMap.get("access_token");
    }
}
