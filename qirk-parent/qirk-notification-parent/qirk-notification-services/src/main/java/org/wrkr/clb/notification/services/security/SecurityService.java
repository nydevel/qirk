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
package org.wrkr.clb.notification.services.security;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wrkr.clb.common.crypto.TokenGenerator;
import org.wrkr.clb.common.crypto.token.notification.NotificationTokenData;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.notification.services.json.JsonStatusCode;


@Component
public class SecurityService {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityService.class);

    @Autowired
    private TokenGenerator tokenGenerator;

    public Map<String, Object> decryptToken(String token, String IV) {
        try {
            String jsonTokenData = tokenGenerator.decrypt(token, IV);
            if (LOG.isTraceEnabled()) {
                LOG.trace("TokenData=" + jsonTokenData);
            }
            return JsonUtils.<Object>convertJsonToMapUsingLongForInts(jsonTokenData);
        } catch (Exception e) {
            throw new SecurityException(JsonStatusCode.INVALID_TOKEN, e);
        }
    }

    private boolean validateTokenData(NotificationTokenData tokenData) {
        if (tokenData == null) {
            return false;
        }
        if (tokenData.subscriberId == null || tokenData.notBefore == null || tokenData.notOnOrAfter == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now < tokenData.notBefore || now >= tokenData.notOnOrAfter) {
            return false;
        }
        return true;
    }

    public void validateTokenDataOrThrowSecurityException(NotificationTokenData tokenData)
            throws SecurityException {
        if (!validateTokenData(tokenData)) {
            throw new SecurityException(JsonStatusCode.INVALID_TOKEN);
        }
    }
}
