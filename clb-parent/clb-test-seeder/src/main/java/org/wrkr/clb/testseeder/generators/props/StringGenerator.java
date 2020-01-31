package org.wrkr.clb.testseeder.generators.props;

import java.util.UUID;

import org.wrkr.clb.testseeder.generators.BaseGenerator;

/**
 * @author Denis Bilenko
 */
public class StringGenerator implements BaseGenerator<String> {

    private static StringGenerator instance = new StringGenerator();
    private StringGenerator() {}
    public static StringGenerator getInstance() {
        return instance;
    }
    
    public String generate() {
        return UUID.randomUUID().toString();
    }

    public String generateTwoSymbols() {
        return firstTwo(generate());
    }
    
    private String firstTwo(String str) {
        return str.substring(0, Math.min(2, str.length()));
    }
}
