package org.wrkr.clb.testseeder.generators.props;

import java.util.Random;

import org.wrkr.clb.testseeder.generators.BaseGenerator;

/**
 * @author Denis Bilenko
 */
public class BooleanGenerator implements BaseGenerator<Boolean> {

    private Random random = new Random();
    private static BooleanGenerator instance = new BooleanGenerator();
    private BooleanGenerator() {}
    
    public static BooleanGenerator getInstance() {
        return instance;
    }

    public Boolean generate() {
        return random.nextBoolean();
    }
    
    public Boolean generate(float probabilityOfTrueInPercent) {
        probabilityOfTrueInPercent /= 100;
        return random.nextFloat() < probabilityOfTrueInPercent;
    }
}
