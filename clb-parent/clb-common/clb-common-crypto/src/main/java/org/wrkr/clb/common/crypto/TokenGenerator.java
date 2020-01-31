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
package org.wrkr.clb.common.crypto;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wrkr.clb.common.crypto.dto.TokenAndIvDTO;

public class TokenGenerator {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(TokenGenerator.class);

    private static final String SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_SPEC_ITERATION_COUNT = 64;
    private static final int KEY_SPEC_KEY_LENGTH = 256;
    private static final String SECRET_KEY_SPEC_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private char[] key;
    private byte[] salt = "sRHkaKGu2QoH/0sSZBd7OylCZvM2TwHj1o5Z".getBytes();

    public void setKey(String key) {
        this.key = key.toCharArray();
    }

    public TokenAndIvDTO encrypt(String plainText) throws GeneralSecurityException {
        byte[] plainBytes = plainText.getBytes();

        // Create SecretKeySpec
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(key, salt, KEY_SPEC_ITERATION_COUNT, KEY_SPEC_KEY_LENGTH);
        SecretKeySpec keySpec = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), SECRET_KEY_SPEC_ALGORITHM);

        // Create IvParameterSpec
        byte[] IV = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(IV);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        // Get Cipher Instance
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);

        // Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        // Perform Encryption
        byte[] cipherBytes = cipher.doFinal(plainBytes);
        return new TokenAndIvDTO(cipherBytes, IV);
    }

    public String decrypt(String token, String ivString) throws GeneralSecurityException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY_ALGORITHM);
        KeySpec spec = new PBEKeySpec(key, salt, KEY_SPEC_ITERATION_COUNT, KEY_SPEC_KEY_LENGTH);
        SecretKeySpec keySpec = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), SECRET_KEY_SPEC_ALGORITHM);

        byte[] IV = Base64.getDecoder().decode(ivString);
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        return new String(cipher.doFinal(Base64.getDecoder().decode(token)));
    }
}
