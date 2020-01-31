package org.wrkr.clb.testseeder.generators.props;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.wrkr.clb.testseeder.generators.BaseGenerator;

/**
 * @author Denis Bilenko
 */
public class HashGenarator implements BaseGenerator<String> {

    private static final String ALGORITHM = "SHA-512";
    private static StringGenerator sg = StringGenerator.getInstance();
    private MessageDigest md;

    private static HashGenarator instance;

    private HashGenarator() {
        try {
            md = MessageDigest.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static HashGenarator getInstance() {
        if (instance == null) {
            instance = new HashGenarator();
        }
        return instance;
    }
    
    public String generate() {
        String randomStr = sg.generate();
        return encryptThisString(randomStr);
    }

    public synchronized static String encryptThisString(String input) {
        byte[] messageDigest = 
                instance.md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }
}
