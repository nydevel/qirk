package org.wrkr.clb.testseeder.generators.props;

import java.util.Random;

import org.wrkr.clb.testseeder.generators.BaseGenerator;

/**
 * @author Denis Bilenko
 */
public class LongGenerator implements BaseGenerator<Long> {

    private Random random = new Random();
    private static LongGenerator instance = new LongGenerator();
    private LongGenerator() {}
    
    public static LongGenerator getInstance() {
        return instance;
    }
    
    public Long generate() {
        return random.nextLong();
    }
    
    public Long generateWithin(Long min, Long max) {
        return min + (long) (Math.random() * (max - min));
    }

}
