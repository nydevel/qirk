package org.wrkr.clb.services.api;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;


@Validated
public interface OAuthService {

    public String getToken(@NotNull(message = "code must not be null") String code,
            @NotNull(message = "redirectURI must not be null") String redirectURI) throws Exception;
}
