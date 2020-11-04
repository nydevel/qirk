package org.wrkr.clb.common.crypto.dto;

import java.util.Base64;

/**
 * @summary DTO used to return the result of encryption by TokenGenerator
 * 
 */
public class TokenAndIvDTO {

    public String token;

    public String IV;

    public TokenAndIvDTO() {
    }

    public TokenAndIvDTO(byte[] token, byte[] IV) {
        this.token = Base64.getEncoder().encodeToString(token);
        this.IV = Base64.getEncoder().encodeToString(IV);
    }
}
