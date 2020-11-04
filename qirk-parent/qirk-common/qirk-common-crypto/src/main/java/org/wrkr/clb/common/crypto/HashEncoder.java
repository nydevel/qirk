package org.wrkr.clb.common.crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.wrkr.clb.common.util.strings.ExtStringUtils;

public class HashEncoder {

    public static byte[] encrypt(String plainText) {
        // getInstance() method is called with algorithm SHA-512
        MessageDigest digester;
        try {
            digester = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // calculate message digest of the input string
        return digester.digest(plainText.getBytes());
    }

    public static String encryptToHex(String plainText) {
        byte[] digestedText = encrypt(plainText);

        // Convert byte array into signum representation
        // Convert message digest into hex value
        String hashText = (new BigInteger(1, digestedText)).toString(16);

        // Add preceding 0s to make it 32 bit
        return ExtStringUtils.addLeadingCharacter(hashText, '0', 32);
    }
}
