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
package org.wrkr.clb.services.api.grecaptcha.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.wrkr.clb.services.api.grecaptcha.GRecaptchaService;


//@Service configured in clb-services-ctx.xml
public class DefaultGRecaptchaService implements GRecaptchaService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultGRecaptchaService.class);

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    // config value
    private String recaptchaSecret;

    public String getRecaptchaSecret() {
        return recaptchaSecret;
    }

    public void setRecaptchaSecret(String recaptchaSecret) {
        this.recaptchaSecret = recaptchaSecret;
    }

    @Override
    public boolean verifyRecaptcha(String ip, String recaptchaResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);

        List<String> parameters = new ArrayList<String>();
        parameters.add("secret=" + recaptchaSecret);
        parameters.add("response=" + recaptchaResponse);
        parameters.add("remoteip=" + ip);

        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate
                .postForEntity(RECAPTCHA_VERIFY_URL + "?" + String.join("&", parameters), request, Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = response.getBody();
        if (LOG.isInfoEnabled()) {
            LOG.info("Recaptcha  error codes: " + responseBody.get("error-codes"));
        }

        Boolean recaptchaSuccess = (Boolean) responseBody.get("success");
        return (recaptchaSuccess != null && recaptchaSuccess);
    }
}
