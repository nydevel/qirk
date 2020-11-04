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
