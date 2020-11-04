package org.wrkr.clb.services.user;

import org.wrkr.clb.model.user.EmailActivationToken;

public interface EmailActivationTokenService {

    public String generatePassword();

    public EmailActivationToken getOrCreate(String email);

    public EmailActivationToken getAndDelete(String token) throws Exception;
}
