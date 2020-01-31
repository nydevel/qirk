package org.wrkr.clb.testseeder.generators.props;

import org.wrkr.clb.testseeder.generators.BaseGenerator;

/**
 * @author Denis Bilenko
 */
public class EmailGenerator implements BaseGenerator<String> {

    private static EmailGenerator instance = new EmailGenerator();
    private EmailGenerator() {}
    public static EmailGenerator getInstance() {
        return instance;
    }
    
    public String generate() {
        StringGenerator sg = StringGenerator.getInstance();
        return sg.generate()+"@gmail.com";
    }
}
