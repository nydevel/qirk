package org.wrkr.clb.chat.services.security;

import java.util.Map;

import org.wrkr.clb.common.crypto.token.chat.SecurityTokenData;

public interface SecurityService {

    public Map<String, Object> decryptToken(String token, String IV);

    public boolean validateTokenData(SecurityTokenData tokenData, boolean validateForWrite);

    public void validateTokenDataOrThrowSecurityException(SecurityTokenData tokenData, boolean validateForWrite)
            throws SecurityException;
}
